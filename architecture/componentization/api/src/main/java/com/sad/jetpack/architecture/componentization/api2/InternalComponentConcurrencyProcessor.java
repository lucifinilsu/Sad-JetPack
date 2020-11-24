package com.sad.jetpack.architecture.componentization.api2;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import androidx.annotation.NonNull;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.sad.core.async.ISADTaskProccessListener;
import com.sad.core.async.SADHandlerAssistant;
import com.sad.core.async.SADTaskRunnable;
import com.sad.core.async.SADTaskSchedulerClient;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

final class InternalComponentConcurrencyProcessor implements IComponentConcurrencyProcessor<InternalComponentConcurrencyProcessor>{
    private IConcurrencyMessageBoundaryInterceptor boundaryInterceptor;
    private String processorId ="";
    private long timeout=-1;
    private long delay=-1;
    private LinkedHashMap<Object,String> units =new LinkedHashMap<>();
    private List<Object> keyList=new ArrayList<>();
    private IPCComponentProcessorSession processorSession;
    private CountDownLatch countDownLatch;
    private ConcurrentLinkedHashMap<Message, String> outPutMessagesMap;
    protected ConcurrentLinkedHashMap<Message, String> getMessageMap() {
        return outPutMessagesMap;
    }
    protected CountDownLatch getCountDownLatch(){
        return countDownLatch;
    }
    protected InternalComponentConcurrencyProcessor(IConcurrencyMessageBoundaryInterceptor boundaryInterceptor, String processorId, long timeout, long delay){
        this.boundaryInterceptor=boundaryInterceptor;
        this.processorId = processorId;
        this.timeout=timeout;
        this.delay=delay;
        this.outPutMessagesMap=new ConcurrentLinkedHashMap.Builder<Message,String>().build();
    }


    @Override
    public InternalComponentConcurrencyProcessor join(IComponent component, String curl) {
        units.put(component,curl);
        return this;
    }

    @Override
    public InternalComponentConcurrencyProcessor join(LinkedHashMap<IComponent, String> components) {
        units.putAll(components);
        return this;
    }

    @Override
    public InternalComponentConcurrencyProcessor join(IComponentRepository repository) {
        if (repository!=null){
            units.putAll(repository.componentInstances());
        }
        return this;
    }

    @Override
    public InternalComponentConcurrencyProcessor join(IComponentProcessor processor) {
        units.put(processor,processor.processorId());
        return this;
    }

    @Override
    public String processorId() {
        return this.processorId;
    }

    @Override
    public InternalComponentConcurrencyProcessor processorSession(IPCComponentProcessorSession processorSession) {
        this.processorSession=processorSession;
        return this;
    }

    @Override
    public IPCComponentProcessorSession processorSession() {
        return this.processorSession;
    }

    @Override
    public long timeout() {
        return this.timeout;
    }

    @Override
    public long delay() {
        return this.delay;
    }

    @Override
    public void submit(Message message) {
        if (message==null){
            message=Message.obtain();
        }
        if (units.isEmpty()){
            if (processorSession!=null){
                processorSession.onProcessorOutput(processorId,message);
            }
            return;
        }
        if (processorSession!=null){
            message=processorSession.onProcessorInput(processorId,message);
        }
        startProceed(message,processorSession);
    }

    private void startProceed(Message message, IPCComponentProcessorSession processorSession){
        if (delay>0){
            SADHandlerAssistant.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    proceed(message,processorSession);
                }
            },delay);
        }
        else {
            proceed(message,processorSession);
        }
    }

    private void proceed(Message message, IPCComponentProcessorSession processorSession){
        try {
            doProceed(message,processorSession);
        }catch (Exception e){
            e.printStackTrace();
            if (processorSession!=null){
                IPCMessageTransmissionConfig transmissionConfig=
                        MessageCreator.standardMessage(message, processorId,delay,timeout,IPCTarget.PROCESSOR_MODE_CONCURRENCY);
                processorSession.onException(transmissionConfig,e);
            }
        }
    }
    private void doProceed(Message msg, IPCComponentProcessorSession processorSession){
        try {
            countDownLatch=new CountDownLatch(units.size());
            SADTaskSchedulerClient.newInstance().execute(new SADTaskRunnable<ConcurrentLinkedHashMap<Message, String>>("Terminal_WaittingFor_Result", new ISADTaskProccessListener<ConcurrentLinkedHashMap<Message, String>>() {
                @Override
                public void onSuccess(ConcurrentLinkedHashMap<Message, String> messages) {
                    if (processorSession!=null){
                        processorSession.onProcessorOutput(messages);
                    }
                    if (boundaryInterceptor!=null){
                        Message msg=boundaryInterceptor.handleMessages(messages);
                        if (processorSession !=null){
                            processorSession.onProcessorOutput(processorId,msg);
                        }
                    }
                }
                @Override
                public void onFail(Throwable throwable) {
                    if (processorSession !=null){
                        IPCMessageTransmissionConfig transmissionConfig=MessageCreator.standardMessage(msg, processorId,delay,timeout,IPCTarget.PROCESSOR_MODE_SEQUENCE);
                        processorSession.onException(transmissionConfig,throwable);
                    }
                }

                @Override
                public void onCancel() {
                }
            }) {
                @Override
                public ConcurrentLinkedHashMap<Message, String> doInBackground() throws Exception {
                    if (timeout>0){
                        Log.e("sad-jetpack","------------->超时设定:"+timeout);
                        if (!countDownLatch.await(timeout, TimeUnit.MILLISECONDS)){
                            throw new TimeoutException("the task of the unit whose type is CONCURRENCY_PROCESSOR is timeout !!!");
                        }
                    }
                    else {
                        countDownLatch.await();
                    }
                    return getMessageMap();
                }
            });
            keyList=new ArrayList<>(units.keySet());
            for (Object o:keyList
            ) {
                Message message=Message.obtain(msg);
                if (o instanceof IComponent){
                    IComponent component= (IComponent) o;
                    Messenger replyMessengerProxy=new Messenger(new ProxyHandler(this,processorSession,component.instanceOrgUrl()));
                    message.replyTo=replyMessengerProxy;
                    IPCMessageTransmissionConfig transmissionConfig=MessageCreator.standardMessage(message,component.instanceOrgUrl(),delay,timeout,IPCTarget.PROCESSOR_MODE_CONCURRENCY);
                    IPCLauncher launcher=IPCLauncherImpl.newInstance(InternalContextHolder.get().getContext()).transmissionConfig(transmissionConfig);
                    component.onCall(message,launcher);
                }
                else if (o instanceof IComponentProcessor){
                    IComponentProcessor processor= (IComponentProcessor) o;
                    IPCComponentProcessorSession processorSessionSelf=processor.processorSession();
                    IPCComponentProcessorSession processorSessionProxy=new IPCComponentProcessorSession() {
                        @Override
                        public void onProcessorOutput(String processorId, Message message) {
                            if (processorSessionSelf!=null){
                                processorSessionSelf.onProcessorOutput(processorId,message);
                            }
                            getMessageMap().put(message, processorId);
                            getCountDownLatch().countDown();
                        }

                        @Override
                        public void onProcessorOutput(ConcurrentLinkedHashMap<Message, String> messages) {
                            if (processorSessionSelf!=null){
                                processorSessionSelf.onProcessorOutput(messages);
                            }
                        }

                        @Override
                        public void onComponentChat(String curl, Message message) {
                            if (processorSessionSelf!=null){
                                processorSessionSelf.onComponentChat(curl,message);
                            }
                        }

                        @Override
                        public void onException(IPCMessageTransmissionConfig config,Throwable throwable) {
                            if (processorSessionSelf!=null){
                                processorSessionSelf.onException(config,throwable);
                            }
                            getCountDownLatch().countDown();
                        }
                    };
                    processor.processorSession(processorSessionProxy).submit(message);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static class ProxyHandler extends Handler {
        private WeakReference<IPCComponentProcessorSession> processorSessionWeakReference;
        private WeakReference<String> curlWeakReference;
        private WeakReference<InternalComponentConcurrencyProcessor> concurrencyProcessorWeakReference;
        ProxyHandler(
                InternalComponentConcurrencyProcessor concurrencyProcessor,
                IPCComponentProcessorSession processorSession,
                String curl
        ){
            this.concurrencyProcessorWeakReference =new WeakReference<InternalComponentConcurrencyProcessor>(concurrencyProcessor);
            this.processorSessionWeakReference =new WeakReference<IPCComponentProcessorSession>(processorSession);
            this.curlWeakReference =new WeakReference<String>(curl);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            InternalComponentConcurrencyProcessor componentConcurrencyProcessor=null;
            IPCComponentProcessorSession prcessorSession = null;
            String curl="";
            if (this.concurrencyProcessorWeakReference !=null){
                componentConcurrencyProcessor= this.concurrencyProcessorWeakReference.get();
            }
            if (processorSessionWeakReference !=null){
                prcessorSession= processorSessionWeakReference.get();
            }
            if (curlWeakReference !=null){
                curl= curlWeakReference.get();
            }

            if (prcessorSession!=null){
                prcessorSession.onComponentChat(curl,msg);
            }
            if (componentConcurrencyProcessor!=null){
                try {
                    componentConcurrencyProcessor.getMessageMap().put(msg,curl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                componentConcurrencyProcessor.getCountDownLatch().countDown();
            }
        }
    }
}

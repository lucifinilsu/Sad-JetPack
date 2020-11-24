package com.sad.jetpack.architecture.componentization.api2;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

import androidx.annotation.NonNull;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.sad.core.async.SADHandlerAssistant;
import com.sad.core.async.SADTaskSchedulerClient;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

final class InternalComponentSequenceProcessor implements IComponentSequenceProcessor<InternalComponentSequenceProcessor> {
    private ISequenceMessageBoundaryInterceptor boundaryInterceptor;
    private String processorId ="";
    private long timeout=-1;
    private long delay=-1;
    private LinkedHashMap<Object,String> units =new LinkedHashMap<>();
    private List<Object> keyList=new ArrayList<>();
    private IPCComponentProcessorSession processorSession;
    private AtomicBoolean isTimeout;
    private ScheduledFuture timeoutFuture;
    private void startTimeout(long timeout){
        isTimeout=new AtomicBoolean(false);
        if (timeout>0){
            timeoutFuture= SADTaskSchedulerClient.executeScheduledTask(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    isTimeout.getAndSet(true);
                    return null;
                }
            },timeout);
        }
    }
    private void finishTimeout(){
        if (timeoutFuture!=null && !timeoutFuture.isCancelled() && !timeoutFuture.isDone()){
            timeoutFuture.cancel(true);
        }
    }
    public void updateIndex(int index) {
        this.index=index;
    }
    protected InternalComponentSequenceProcessor(ISequenceMessageBoundaryInterceptor boundaryInterceptor, String processorId, long timeout, long delay){
        this.boundaryInterceptor=boundaryInterceptor;
        this.processorId = processorId;
        this.timeout=timeout;
        this.delay=delay;
    }
    @Override
    public InternalComponentSequenceProcessor join(IComponent component, String curl) {
        units.put(component,curl);
        return this;
    }

    @Override
    public InternalComponentSequenceProcessor join(LinkedHashMap<IComponent, String> components) {
        units.putAll(components);
        return this;
    }

    @Override
    public InternalComponentSequenceProcessor join(IComponentRepository repository) {
        if (repository!=null){
            units.putAll(repository.componentInstances());
        }
        return this;
    }

    @Override
    public InternalComponentSequenceProcessor join(IComponentProcessor processor) {
        units.put(processor,processor.processorId());
        return this;
    }

    @Override
    public String processorId() {
        return this.processorId;
    }

    @Override
    public InternalComponentSequenceProcessor processorSession(IPCComponentProcessorSession processorSession) {
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
        keyList=new ArrayList<>(units.keySet());
        startProceed(message,processorSession);
    }

    private void startProceed(Message message,IPCComponentProcessorSession processorSession){
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

    private void proceed(Message message,IPCComponentProcessorSession processorSession){
        try {
            startTimeout(timeout);
            doProceed(message,processorSession);
        } catch (Exception e) {
            e.printStackTrace();
            if (processorSession!=null){
                message.obj=e;
                IPCMessageTransmissionConfig transmissionConfig=MessageCreator.standardMessage(message, processorId,delay,timeout,IPCTarget.PROCESSOR_MODE_SEQUENCE);
                processorSession.onException(transmissionConfig,e);
            }
        }
    }

    private int index=-1;
    public void doProceed(Message message, IPCComponentProcessorSession processorSession){
        try {
            if (isTimeout.get()){
                throw new TimeoutException("the processor of the unit whose type is SEQUENCE_PROCESSOR is timeout !!!");
            }
            if (index== units.size()-1){
                finishTimeout();
                //最后一个
                if (boundaryInterceptor!=null){
                    message=boundaryInterceptor.handleMessage(message);
                }
                if (processorSession!=null){
                    processorSession.onProcessorOutput(processorId,message);
                }
                return;
            }
            index++;
            Object o= keyList.get(index+1);
            if (o instanceof IComponent){
                IComponent component= (IComponent)o;
                IPCMessageTransmissionConfig transmissionConfig=MessageCreator.standardMessage(message,component.instanceOrgUrl(),delay,timeout,IPCTarget.PROCESSOR_MODE_SEQUENCE);
                IPCLauncher launcher=IPCLauncherImpl.newInstance(InternalContextHolder.get().getContext()).transmissionConfig(transmissionConfig);
                Messenger replyMessengerProxy=new Messenger(new ProxyHandler(this,processorSession,component.instanceOrgUrl()));
                message.replyTo=replyMessengerProxy;
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
                        doProceed(message,processorSession);
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
                    public void onException(IPCMessageTransmissionConfig transmissionConfig,Throwable throwable) {
                        if (processorSessionSelf!=null){
                            processorSessionSelf.onException(transmissionConfig,throwable);
                        }
                        //串行环节在异常的情况下直接断路
                        if (processorSession!=null){
                            processorSession.onException(transmissionConfig,throwable);
                        }
                    }
                };
                processor.processorSession(processorSessionProxy).submit(message);
            }
        }catch (Exception e){
            e.printStackTrace();
            if (processorSession!=null){
                IPCMessageTransmissionConfig transmissionConfig=MessageCreator.standardMessage(message, processorId,delay,timeout,IPCTarget.PROCESSOR_MODE_SEQUENCE);
                processorSession.onException(transmissionConfig,e);
            }
        }


    }
    private static class ProxyHandler extends Handler {
        private WeakReference<IPCComponentProcessorSession> processorSessionWeakReference;
        private WeakReference<String> urlWeakReference;
        private WeakReference<InternalComponentSequenceProcessor> sequenceProcessorWeakReference;
        ProxyHandler(
                InternalComponentSequenceProcessor sequenceProcessor,
                IPCComponentProcessorSession processorSession,
                String url
        ){
            this.sequenceProcessorWeakReference =new WeakReference<InternalComponentSequenceProcessor>(sequenceProcessor);
            this.processorSessionWeakReference =new WeakReference<IPCComponentProcessorSession>(processorSession);
            this.urlWeakReference =new WeakReference<String>(url);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            InternalComponentSequenceProcessor sequenceProcessor=null;
            IPCComponentProcessorSession prcessorSession = null;
            String url=null;
            IPCMessageTransmissionConfig transmissionConfig=null;
            if (this.sequenceProcessorWeakReference !=null){
                sequenceProcessor= this.sequenceProcessorWeakReference.get();
            }
            if (processorSessionWeakReference !=null){
                prcessorSession= processorSessionWeakReference.get();
            }
            if (urlWeakReference !=null){
                url= urlWeakReference.get();
            }
            if (prcessorSession!=null){
                prcessorSession.onComponentChat(url,msg);
            }
            if (sequenceProcessor!=null){
                sequenceProcessor.doProceed(msg,prcessorSession);
            }
        }
    }
}

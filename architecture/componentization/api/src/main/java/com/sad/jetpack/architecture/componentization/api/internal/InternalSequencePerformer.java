package com.sad.jetpack.architecture.componentization.api.internal;

import com.sad.core.async.SADHandlerAssistant;
import com.sad.core.async.SADTaskSchedulerClient;
import com.sad.jetpack.architecture.componentization.api.DataState;
import com.sad.jetpack.architecture.componentization.api.IProceedListener;
import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IPCMessenger;
import com.sad.jetpack.architecture.componentization.api.IPCSession;
import com.sad.jetpack.architecture.componentization.api.IPerformer;
import com.sad.jetpack.architecture.componentization.api.impl.DefaultIPCMessenger;
import com.sad.jetpack.architecture.componentization.api.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class InternalSequencePerformer implements IPerformer {
    private LinkedHashMap<IExposedService,String> exposedServices=new LinkedHashMap<>();
    private IProceedListener callerListener;
    private long timeout=-1;
    private ScheduledFuture scheduledFuture;
    public InternalSequencePerformer(LinkedHashMap<IExposedService,String> exposedServices) {
        this.exposedServices = exposedServices;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setCallerListener(IProceedListener callerListener) {
        this.callerListener = callerListener;
    }

    private void doStartProceed(IDataCarrier inputData){
        if (callerListener!=null){
            inputData=callerListener.onInput(inputData);
        }
        startTimeout(timeout);
        proceed(inputData);
    }
    private AtomicBoolean isTimeout;
    private void startTimeout(long timeout){
        isTimeout=new AtomicBoolean(false);
        if (timeout!=-1){
            scheduledFuture=SADTaskSchedulerClient.executeScheduledTask(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    isTimeout.getAndSet(true);
                    return null;
                }
            },timeout);
        }
    }

    private void finishTimeout(){
        if (scheduledFuture!=null && !scheduledFuture.isCancelled() && !scheduledFuture.isDone()){
            scheduledFuture.cancel(true);
        }
    }

    private int index=-1;
    //private ConcurrentLinkedHashMap<String,IDataCarrier> outputData=new ConcurrentLinkedHashMap.Builder<String,IDataCarrier>().build();
    private void proceed(IDataCarrier data){
        if (isTimeout.get()){
            if (callerListener!=null){
                callerListener.onExceptionInPerformer(new TimeoutException("InternalSequencePerformer's task timeout !!!"));
            }
            return;
        }
        if (index==exposedServices.size()){
            //已执行完最后一个，调用回调结束
            if (callerListener!=null){
                finishTimeout();
                callerListener.onOutput(data);
            }
            return;
        }
        List<IExposedService> es=new ArrayList<>(exposedServices.keySet());
        List<String> us=new ArrayList<>(exposedServices.values());
        IExposedService exposedService=es.get(index);
        String orgUrl=us.get(index);
        IPCMessenger messengerProxy=new DefaultIPCMessenger(Utils.encodeMessengerId(orgUrl,""+index)) {
            @Override
            public boolean reply(IDataCarrier d, IPCSession session) {
                d.creator().state(DataState.RUNNING);
                boolean intercepted= callerListener!=null?callerListener.onProceed(InternalSequencePerformer.this,d,session,messengerId()):false;
                if (!intercepted){
                    //继续执行下一个
                    index++;
                    proceed(d);
                    return false;
                }
                else {
                    //外部要求中止串行，调用回调
                    if (callerListener!=null){
                        finishTimeout();
                        d.creator().state(DataState.INTERCEPTED);
                        callerListener.onIntercepted(InternalSequencePerformer.this,exposedService,d);
                    }
                }
                return true;
            }

            @Override
            public IDataCarrier extraMessage() {
                return data.creator().state(DataState.UNWORKED).create();
            }

        };
        exposedService.action(messengerProxy);
    }


    @Override
    public void start(IDataCarrier data, boolean restart,long delay) {
        try {
            if (restart){
                index=0;
            }
            else {
                index++;
            }
            if (delay>0){
                SADHandlerAssistant.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        doStartProceed(data);
                    }
                },delay);
            }
            else {
                doStartProceed(data);
            }
        }catch (Exception e){
            e.printStackTrace();
            if (callerListener!=null){
                callerListener.onExceptionInPerformer(e);
            }
        }

    }
}

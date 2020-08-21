package com.sad.jetpack.architecture.componentization.api.internal;

import com.sad.core.async.SADHandlerAssistant;
import com.sad.jetpack.architecture.componentization.api.DataState;
import com.sad.jetpack.architecture.componentization.api.ICallerListener;
import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IPCMessenger;
import com.sad.jetpack.architecture.componentization.api.IPCSession;
import com.sad.jetpack.architecture.componentization.api.IPerformer;
import com.sad.jetpack.architecture.componentization.api.impl.AbsIPCMessenger;
import com.sad.jetpack.architecture.componentization.api.impl.DataCarrierImpl;

import java.util.ArrayList;
import java.util.List;

public class InternalSequencePerformer implements IPerformer {
    private List<IExposedService> exposedServices=new ArrayList<>();
    private ICallerListener callerListener;
    private long timeout=-1;
    public InternalSequencePerformer(List<IExposedService> exposedServices) {
        this.exposedServices = exposedServices;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setCallerListener(ICallerListener callerListener) {
        this.callerListener = callerListener;
    }

    private void doStartProceed(IDataCarrier inputData){
        if (callerListener!=null){
            inputData=callerListener.onStartToProceedExposedServiceGroup(inputData);
        }
        proceed(inputData);
    }

    private int index=-1;

    private void proceed(IDataCarrier data){
        if (index==exposedServices.size()){
            //已执行完最后一个，调用回调结束
            if (callerListener!=null){
                callerListener.onEndExposedServiceGroup(data);
            }
            return;
        }
        IExposedService exposedService=exposedServices.get(index);
        IPCMessenger messengerProxy=new AbsIPCMessenger(index+"") {
            @Override
            public boolean reply(IDataCarrier d, IPCSession session) {
                d.creator().state(DataState.RUNNING);
                boolean intercepted= callerListener!=null?callerListener.onProceedExposedService(InternalSequencePerformer.this,d,session,messengerId()):false;
                if (!intercepted){
                    //继续执行下一个
                    index++;
                    proceed(d);
                    return false;
                }
                else {
                    //外部要求中止串行，调用回调
                    if (callerListener!=null){
                        d.creator().state(DataState.INTERCEPTED);
                        callerListener.onIntercepted(InternalSequencePerformer.this,exposedService,d);
                    }
                }
                return true;
            }

            @Override
            public IDataCarrier extraMessage() {
                IDataCarrier dataCarrier=DataCarrierImpl.newInstanceCreator()
                        .data(data)
                        .state(DataState.UNWORKED)
                        .create()
                        ;
                return dataCarrier;
            }
        };
        exposedService.action(messengerProxy);
    }


    @Override
    public void start(IDataCarrier data, boolean restart,long delay) {
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
    }
}

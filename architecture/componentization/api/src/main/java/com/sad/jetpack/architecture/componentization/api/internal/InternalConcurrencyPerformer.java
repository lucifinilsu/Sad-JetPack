package com.sad.jetpack.architecture.componentization.api.internal;

import android.util.Log;

import com.sad.core.async.ISADTaskProccessListener;
import com.sad.core.async.SADHandlerAssistant;
import com.sad.core.async.SADTaskRunnable;
import com.sad.core.async.SADTaskSchedulerClient;
import com.sad.jetpack.architecture.componentization.api.DataState;
import com.sad.jetpack.architecture.componentization.api.ICallerListener;
import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IPCSession;
import com.sad.jetpack.architecture.componentization.api.IPerformer;
import com.sad.jetpack.architecture.componentization.api.impl.AbsIPCMessenger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class InternalConcurrencyPerformer implements IPerformer {
    private List<IExposedService> exposedServices=new ArrayList<>();
    private ICallerListener callerListener;
    private long timeout=-1;
    private ScheduledFuture scheduledFuture;
    public InternalConcurrencyPerformer(List<IExposedService> exposedServices) {
        this.exposedServices = exposedServices;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setCallerListener(ICallerListener callerListener) {
        this.callerListener = callerListener;
    }


    @Override
    public void start(IDataCarrier data, boolean restart, long delay) {
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
    private CountDownLatch countDownLatch;
    private void proceed(IDataCarrier inputData){
        countDownLatch=new CountDownLatch(exposedServices.size());
        for (IExposedService exposedService:exposedServices
        ) {
            exposedService.action(new AbsIPCMessenger(exposedServices.indexOf(exposedService)+"") {
                @Override
                public boolean reply(IDataCarrier d, IPCSession session) {
                    inputData.creator().data(d.data()).state(DataState.RUNNING);
                    countDownLatch.countDown();
                    if (callerListener!=null){
                        callerListener.onProceedExposedService(InternalConcurrencyPerformer.this,inputData,session,messengerId());
                    }
                    return false;
                }
                @Override
                public IDataCarrier extraMessage() {
                    return inputData.creator().state(DataState.UNWORKED).create();
                }
            });
        }
        SADTaskSchedulerClient.newInstance().execute(new SADTaskRunnable<IDataCarrier>("Terminal_WaittingFor_Result", new ISADTaskProccessListener<IDataCarrier>() {
            @Override
            public void onSuccess(IDataCarrier result) {
                result.creator().state(DataState.DONE);
                if (callerListener!=null){
                    callerListener.onEndExposedServiceGroup(result);
                }
            }

            @Override
            public void onFail(Throwable throwable) {
                inputData.creator().state(DataState.EXCEPTION);
                if (callerListener!=null){
                    callerListener.onFailureExposedServiceGroup(inputData,throwable);
                }
            }

            @Override
            public void onCancel() {
            }
        }) {
            @Override
            public IDataCarrier doInBackground() throws Exception {
                if (timeout>0){
                    Log.e("sad-jetpack","------------->超时设定:"+timeout);
                    if (!countDownLatch.await(timeout, TimeUnit.SECONDS)){
                        throw new TimeoutException("InternalConcurrencyPerformer's task timeout !!!");
                    }
                }
                else {
                    countDownLatch.await();
                }
                return inputData;
            }
        });
    }
    private void doStartProceed(IDataCarrier data){
        if (callerListener!=null){
            data=callerListener.onStartToProceedExposedServiceGroup(data);
        }
        proceed(data);
    }
}

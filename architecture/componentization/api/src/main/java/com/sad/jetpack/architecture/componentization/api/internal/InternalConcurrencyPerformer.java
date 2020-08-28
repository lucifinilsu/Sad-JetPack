package com.sad.jetpack.architecture.componentization.api.internal;

import android.util.Log;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.sad.core.async.ISADTaskProccessListener;
import com.sad.core.async.SADHandlerAssistant;
import com.sad.core.async.SADTaskRunnable;
import com.sad.core.async.SADTaskSchedulerClient;
import com.sad.jetpack.architecture.componentization.api.DataState;
import com.sad.jetpack.architecture.componentization.api.IProceedListener;
import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IPCSession;
import com.sad.jetpack.architecture.componentization.api.IPerformer;
import com.sad.jetpack.architecture.componentization.api.impl.DefaultIPCMessenger;
import com.sad.jetpack.architecture.componentization.api.impl.DataCarrierImpl;
import com.sad.jetpack.architecture.componentization.api.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class InternalConcurrencyPerformer implements IPerformer {
    private LinkedHashMap<IExposedService,String> exposedServices=new LinkedHashMap<>();
    private IProceedListener callerListener;
    private long timeout=-1;
    public InternalConcurrencyPerformer(LinkedHashMap<IExposedService,String> exposedServices) {
        this.exposedServices = exposedServices;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setCallerListener(IProceedListener callerListener) {
        this.callerListener = callerListener;
    }


    @Override
    public void start(IDataCarrier data, boolean restart, long delay) {
        try {
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
    private CountDownLatch countDownLatch;
    private void proceed(IDataCarrier orgInputData){
        countDownLatch=new CountDownLatch(exposedServices.size());
        List<IExposedService> es=new ArrayList<>(exposedServices.keySet());
        List<String> us=new ArrayList<>(exposedServices.values());
        ConcurrentLinkedHashMap<String,IDataCarrier> outPut=new ConcurrentLinkedHashMap.Builder<String,IDataCarrier>().maximumWeightedCapacity(exposedServices.size()).build();
        for (IExposedService exposedService:es
        ) {
            int index=es.indexOf(exposedService);
            String orgUrl=us.get(index);
            IDataCarrier inputData= DataCarrierImpl.newInstanceCreator().data(orgInputData.data()).state(DataState.RUNNING).create();
            exposedService.action(new DefaultIPCMessenger(Utils.encodeMessengerId(orgUrl,""+index)) {

                @Override
                public boolean reply(IDataCarrier d, IPCSession session) {
                    outPut.put(messengerId(),d.creator().state(DataState.DONE).create());
                    countDownLatch.countDown();
                    if (callerListener!=null){
                        callerListener.onProceed(InternalConcurrencyPerformer.this,d,session,messengerId());
                    }
                    return false;
                }
            }.extraMessage(inputData));
        }
        SADTaskSchedulerClient.newInstance().execute(new SADTaskRunnable<ConcurrentLinkedHashMap<String,IDataCarrier>>("Terminal_WaittingFor_Result", new ISADTaskProccessListener<ConcurrentLinkedHashMap<String,IDataCarrier>>() {
            @Override
            public void onSuccess(ConcurrentLinkedHashMap<String,IDataCarrier> outputData) {

                if (callerListener!=null){
                    callerListener.onOutput(outputData);
                }
            }

            @Override
            public void onFail(Throwable throwable) {
                if (callerListener!=null){
                    callerListener.onExceptionInPerformer(throwable);
                }
            }

            @Override
            public void onCancel() {
            }
        }) {
            @Override
            public ConcurrentLinkedHashMap<String,IDataCarrier> doInBackground() throws Exception {
                if (timeout>0){
                    Log.e("sad-jetpack","------------->超时设定:"+timeout);
                    if (!countDownLatch.await(timeout, TimeUnit.SECONDS)){
                        throw new TimeoutException("InternalConcurrencyPerformer's task timeout !!!");
                    }
                }
                else {
                    countDownLatch.await();
                }
                return outPut;
            }
        });
    }
    private void doStartProceed(IDataCarrier data){
        if (callerListener!=null){
            data=callerListener.onInput(data);
        }
        proceed(data);
    }
}

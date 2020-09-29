package com.sad.jetpack.architecture.componentization.api.internal;

import android.util.Log;

import androidx.annotation.NonNull;

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
    private IProceedListener proceedListener;
    private long timeout=-1;
    public InternalConcurrencyPerformer(LinkedHashMap<IExposedService,String> exposedServices) {
        this.exposedServices = exposedServices;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setProceedListener(IProceedListener proceedListener) {
        this.proceedListener = proceedListener;
    }


    @Override
    public void start(@NonNull IDataCarrier data, boolean restart, long delay) {
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
            if (proceedListener !=null){
                proceedListener.onExceptionInPerformer(e);
            }
        }

    }
    private CountDownLatch countDownLatch;
    private void proceed(@NonNull IDataCarrier orgInputData){
        countDownLatch=new CountDownLatch(exposedServices.size());
        List<IExposedService> es=new ArrayList<>(exposedServices.keySet());
        List<String> us=new ArrayList<>(exposedServices.values());
        ConcurrentLinkedHashMap<String,IDataCarrier> outPut=new ConcurrentLinkedHashMap.Builder<String,IDataCarrier>().maximumWeightedCapacity(exposedServices.size()).build();
        for (IExposedService exposedService:es
        ) {
            int index=es.indexOf(exposedService);
            String orgUrl=us.get(index);
            Object dd=orgInputData==null?null:orgInputData.data();
            IDataCarrier inputData= DataCarrierImpl.newInstanceCreator().data(dd).state(DataState.RUNNING).create();
            exposedService.action(new DefaultIPCMessenger(Utils.encodeMessengerId(orgUrl,""+index)) {

                @Override
                public boolean reply(IDataCarrier d, IPCSession session) {
                    if (d!=null){
                        d=d.creator().state(DataState.DONE).create();
                    }
                    outPut.put(messengerId(),d);
                    countDownLatch.countDown();
                    if (proceedListener !=null){
                        proceedListener.onProceed(d,session,messengerId());
                    }
                    return false;
                }
            }.extraMessage(inputData));
        }
        SADTaskSchedulerClient.newInstance().execute(new SADTaskRunnable<ConcurrentLinkedHashMap<String,IDataCarrier>>("Terminal_WaittingFor_Result", new ISADTaskProccessListener<ConcurrentLinkedHashMap<String,IDataCarrier>>() {
            @Override
            public void onSuccess(ConcurrentLinkedHashMap<String,IDataCarrier> outputData) {

                if (proceedListener !=null){
                    proceedListener.onOutput(outputData);
                }
            }

            @Override
            public void onFail(Throwable throwable) {
                if (proceedListener !=null){
                    proceedListener.onExceptionInPerformer(throwable);
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
        if (proceedListener !=null){
            data= proceedListener.onInput(data);
        }
        proceed(data);
    }
}

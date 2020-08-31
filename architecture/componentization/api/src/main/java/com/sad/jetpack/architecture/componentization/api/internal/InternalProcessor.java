package com.sad.jetpack.architecture.componentization.api.internal;

import androidx.annotation.NonNull;

import com.sad.jetpack.architecture.componentization.api.IProceedListener;
import com.sad.jetpack.architecture.componentization.api.ICluster;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IProcessor;
import com.sad.jetpack.architecture.componentization.api.IPerformer;

import java.util.LinkedHashMap;

public class InternalProcessor implements IProcessor {

    private LinkedHashMap<Object,String> extraObjects=new LinkedHashMap<>();
    private LinkedHashMap<IExposedService,String> exposedServices=new LinkedHashMap<>();
    private int proceedMode = IProcessor.PROCEED_MODE_SEQUENCE;
    private long timeout=-1;
    private IProceedListener callerListener;
    public InternalProcessor(){}
    public InternalProcessor(LinkedHashMap<Object,String> extraObjects, LinkedHashMap<IExposedService,String> exposedServices) {
        this.extraObjects = extraObjects;
        this.exposedServices = exposedServices;
        this.proceedMode = proceedMode;
    }

    @Override
    public IProcessor proceedMode(int processMode) {
        this.proceedMode=processMode;
        return this;
    }

    public void setExposedServices(LinkedHashMap<IExposedService,String> exposedServices) {
        this.exposedServices = exposedServices;
    }

    public void setExtraObjects( LinkedHashMap<Object,String> extraObjects) {
        this.extraObjects = extraObjects;
    }
    @Override
    public  LinkedHashMap<Object,String> extraObjectInstances() {
        return extraObjects;
    }

    @Override
    public LinkedHashMap<IExposedService,String> exposedServiceInstance() {
        return exposedServices;
    }

    @Override
    public IProcessor timeout(long timeout) {
        this.timeout=timeout;
        return this;
    }

    @Override
    public @NonNull IPerformer submit() {
        if (proceedMode == IProcessor.PROCEED_MODE_SEQUENCE){
            InternalSequencePerformer performer= new InternalSequencePerformer(exposedServices);
            performer.setCallerListener(callerListener);
            performer.setTimeout(timeout);
            return performer;
        }
        else if (proceedMode ==IProcessor.PROCEED_MODE_CONCURRENCY){
            InternalConcurrencyPerformer performer=new InternalConcurrencyPerformer(exposedServices);
            performer.setCallerListener(callerListener);
            performer.setTimeout(timeout);
            return performer;
        }
        return null;
    }

    @Override
    public IProcessor listener(IProceedListener callerListener) {
        this.callerListener=callerListener;
        return this;
    }
}

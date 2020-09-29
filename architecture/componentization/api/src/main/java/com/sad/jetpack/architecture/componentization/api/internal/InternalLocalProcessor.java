package com.sad.jetpack.architecture.componentization.api.internal;

import androidx.annotation.NonNull;

import com.sad.jetpack.architecture.componentization.api.IProceedListener;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.ILocalProcessor;
import com.sad.jetpack.architecture.componentization.api.IPerformer;

import java.util.LinkedHashMap;

public class InternalLocalProcessor implements ILocalProcessor<InternalLocalProcessor> {

    private LinkedHashMap<Object,String> extraObjects=new LinkedHashMap<>();
    private LinkedHashMap<IExposedService,String> exposedServices=new LinkedHashMap<>();
    private int proceedMode = ILocalProcessor.PROCEED_MODE_SEQUENCE;
    private long timeout=-1;
    private IProceedListener callerListener;
    public InternalLocalProcessor(){}
    public InternalLocalProcessor(LinkedHashMap<Object,String> extraObjects, LinkedHashMap<IExposedService,String> exposedServices) {
        this.extraObjects = extraObjects;
        this.exposedServices = exposedServices;
    }

    @Override
    public InternalLocalProcessor proceedMode(int processMode) {
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
    public InternalLocalProcessor timeout(long timeout) {
        this.timeout=timeout;
        return this;
    }

    @Override
    public @NonNull IPerformer submit() {
        if (proceedMode == ILocalProcessor.PROCEED_MODE_SEQUENCE){
            InternalSequencePerformer performer= new InternalSequencePerformer(exposedServices);
            performer.setProceedListener(callerListener);
            performer.setTimeout(timeout);
            return performer;
        }
        else if (proceedMode == ILocalProcessor.PROCEED_MODE_CONCURRENCY){
            InternalConcurrencyPerformer performer=new InternalConcurrencyPerformer(exposedServices);
            performer.setProceedListener(callerListener);
            performer.setTimeout(timeout);
            return performer;
        }
        return null;
    }

    @Override
    public InternalLocalProcessor listener(IProceedListener callerListener) {
        this.callerListener=callerListener;
        return this;
    }
}

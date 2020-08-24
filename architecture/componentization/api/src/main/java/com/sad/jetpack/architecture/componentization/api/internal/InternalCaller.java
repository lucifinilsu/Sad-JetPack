package com.sad.jetpack.architecture.componentization.api.internal;

import androidx.annotation.NonNull;

import com.sad.jetpack.architecture.componentization.api.ICallerListener;
import com.sad.jetpack.architecture.componentization.api.ICluster;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.ICaller;
import com.sad.jetpack.architecture.componentization.api.IPerformer;

import java.util.ArrayList;
import java.util.List;

public class InternalCaller implements ICaller {

    private List extraObjects=new ArrayList();
    private List<IExposedService> exposedServices=new ArrayList<>();
    private int callMode= ICluster.CALL_MODE_SEQUENCE;
    private long timeout=-1;
    private ICallerListener callerListener;
    public InternalCaller(int callMode) {
        this.callMode=callMode;
    }
    public InternalCaller(int callMode,List extraObjects, List<IExposedService> exposedServices) {
        this.extraObjects = extraObjects;
        this.exposedServices = exposedServices;
        this.callMode=callMode;
    }

    public void setCallMode(int callMode) {
        this.callMode = callMode;
    }

    public void setExposedServices(List<IExposedService> exposedServices) {
        this.exposedServices = exposedServices;
    }

    public void setExtraObjects(List extraObjects) {
        this.extraObjects = extraObjects;
    }
    @Override
    public List extraObjectInstances() {
        return extraObjects;
    }

    @Override
    public List<IExposedService> exposedServiceInstance() {
        return exposedServices;
    }

    @Override
    public ICaller timeout(long timeout) {
        this.timeout=timeout;
        return this;
    }

    @Override
    public @NonNull IPerformer submit() {
        if (callMode== ICluster.CALL_MODE_SEQUENCE){
            InternalSequencePerformer performer= new InternalSequencePerformer(exposedServices);
            performer.setCallerListener(callerListener);
            performer.setTimeout(timeout);
            return performer;
        }
        else if (callMode==ICluster.CALL_MODE_CONCURRENCY){
            InternalConcurrencyPerformer performer=new InternalConcurrencyPerformer(exposedServices);
            performer.setCallerListener(callerListener);
            performer.setTimeout(timeout);
            return performer;
        }
        return null;
    }

    @Override
    public ICaller listener(ICallerListener callerListener) {
        this.callerListener=callerListener;
        return this;
    }
}

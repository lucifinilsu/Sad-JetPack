package com.sad.jetpack.architecture.componentization.api.internal;

import androidx.annotation.NonNull;

import com.sad.jetpack.architecture.componentization.api.ICallerListener;
import com.sad.jetpack.architecture.componentization.api.ICluster;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IProcessor;
import com.sad.jetpack.architecture.componentization.api.IPerformer;

import java.util.ArrayList;
import java.util.List;

public class InternalProcessor implements IProcessor {

    private List extraObjects=new ArrayList();
    private List<IExposedService> exposedServices=new ArrayList<>();
    private int processMode = ICluster.CALL_MODE_SEQUENCE;
    private long timeout=-1;
    private ICallerListener callerListener;
    public InternalProcessor(int processMode) {
        this.processMode = processMode;
    }
    public InternalProcessor(int processMode, List extraObjects, List<IExposedService> exposedServices) {
        this.extraObjects = extraObjects;
        this.exposedServices = exposedServices;
        this.processMode = processMode;
    }

    public void setProcessMode(int processMode) {
        this.processMode = processMode;
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
    public IProcessor timeout(long timeout) {
        this.timeout=timeout;
        return this;
    }

    @Override
    public @NonNull IPerformer submit() {
        if (processMode == ICluster.CALL_MODE_SEQUENCE){
            InternalSequencePerformer performer= new InternalSequencePerformer(exposedServices);
            performer.setCallerListener(callerListener);
            performer.setTimeout(timeout);
            return performer;
        }
        else if (processMode ==ICluster.CALL_MODE_CONCURRENCY){
            InternalConcurrencyPerformer performer=new InternalConcurrencyPerformer(exposedServices);
            performer.setCallerListener(callerListener);
            performer.setTimeout(timeout);
            return performer;
        }
        return null;
    }

    @Override
    public IProcessor listener(ICallerListener callerListener) {
        this.callerListener=callerListener;
        return this;
    }
}

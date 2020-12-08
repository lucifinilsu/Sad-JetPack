package com.sad.jetpack.architecture.componentization.api;

import java.util.LinkedList;
import java.util.List;

abstract class AbsInternalComponentProcessor implements IComponentProcessor,IComponentProcessor.Builder{

    protected String processorId="";
    protected IComponentProcessorCallListener callListener;
    protected ICallerConfig callerConfig;
    protected List<Object> units =new LinkedList<>();
    protected boolean listenerCrossed=false;
     @Override
     public boolean listenerCrossed() {
         return this.listenerCrossed;
     }

     @Override
    public String processorId() {
        return processorId;
    }

    @Override
    public IComponentProcessorCallListener listener() {
        return callListener;
    }

    @Override
    public ICallerConfig callerConfig() {
        return callerConfig;
    }

    @Override
    public IComponentProcessor join(IComponentCallable componentCallable) {
        units.add(componentCallable);
        return this;
    }

    @Override
    public IComponentProcessor join(List<IComponentCallable> componentCallables) {
        units.addAll(componentCallables);
        return this;
    }

    @Override
    public IComponentProcessor join(IComponentProcessor processor) {
        units.add(processor);
        return this;
    }


    @Override
    public Builder toBuilder() {
        return this;
    }

    @Override
    public Builder listener(IComponentProcessorCallListener listener) {
        this.callListener=listener;
        return this;
    }

     @Override
     public Builder listenerCrossed(boolean crossed) {
         this.listenerCrossed=crossed;
         return this;
     }

     @Override
    public Builder callerConfig(ICallerConfig callerConfig) {
        this.callerConfig=callerConfig;
        return this;
    }

    @Override
    public IComponentProcessor build() {
        return this;
    }

    protected boolean needDelay(){
        return callerConfig!=null && callerConfig.delay()>0;
    }
    protected boolean needCheckTimeout(){
        return callerConfig!=null && callerConfig.timeout()>0;
    }
}

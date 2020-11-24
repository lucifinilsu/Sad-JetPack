package com.sad.jetpack.architecture.componentization.api2;

public class ComponentProcessorBuilderImpl implements IComponentProcessorBuilder{

    private long timeout=10000;
    private long delay=-1;
    private String processorId ="";

    private ComponentProcessorBuilderImpl(String processorId){
        this.processorId = processorId;
    }

    public static IComponentProcessorBuilder newBuilder(String url){
        return new ComponentProcessorBuilderImpl(url);
    }

    @Override
    public InternalComponentSequenceProcessor asSequence() {
        return asSequence(DefaultSequenceMessageBoundaryInterceptor.getInstance());
    }

    @Override
    public InternalComponentSequenceProcessor asSequence(ISequenceMessageBoundaryInterceptor boundaryInterceptor) {
        return new InternalComponentSequenceProcessor(boundaryInterceptor,this.processorId,this.timeout,this.delay);
    }

    @Override
    public InternalComponentConcurrencyProcessor asConcurrency(IConcurrencyMessageBoundaryInterceptor boundaryInterceptor) {
        return new InternalComponentConcurrencyProcessor(boundaryInterceptor,this.processorId,this.timeout,this.delay);
    }

    @Override
    public InternalComponentConcurrencyProcessor asConcurrency() {
        return asConcurrency(DefaultConcurrencyMessageBoundaryInterceptor.getInstance());
    }

    @Override
    public IComponentProcessorBuilder timeout(long time) {
        this.timeout=time;
        return this;
    }

    @Override
    public IComponentProcessorBuilder delay(long time) {
        this.delay=time;
        return this;
    }

    @Override
    public IComponentProcessorBuilder processorId(String processorId) {
        this.processorId = processorId;
        return this;
    }
}

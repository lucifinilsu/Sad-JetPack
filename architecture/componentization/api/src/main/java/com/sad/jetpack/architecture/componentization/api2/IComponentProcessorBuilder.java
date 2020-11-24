package com.sad.jetpack.architecture.componentization.api2;

public interface IComponentProcessorBuilder {

    <I extends IComponentSequenceProcessor<I>> I asSequence();

    <I extends IComponentSequenceProcessor<I>> I asSequence(ISequenceMessageBoundaryInterceptor boundaryInterceptor);

    <I extends IComponentConcurrencyProcessor<I>> I asConcurrency(IConcurrencyMessageBoundaryInterceptor boundaryInterceptor);

    <I extends IComponentConcurrencyProcessor<I>> I asConcurrency();

    IComponentProcessorBuilder timeout(long time);

    IComponentProcessorBuilder delay(long time);

    IComponentProcessorBuilder processorId(String processorId);

}

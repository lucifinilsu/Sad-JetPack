package com.sad.jetpack.architecture.componentization.api;


import java.util.List;

public interface IComponentProcessor extends ISortable{

    boolean listenerCrossed();

    String processorId();

    IComponentProcessorCallListener listener();

    ICallerConfig callerConfig();

    IComponentProcessor join(IComponentCallable componentCallable);

    IComponentProcessor join(List<IComponentCallable> componentCallables);

    IComponentProcessor join(IComponentProcessor processor);

    void submit(IRequest request);

    Builder toBuilder();

    interface Builder{

        Builder listener(IComponentProcessorCallListener listener);

        Builder listenerCrossed(boolean crossed);

        Builder callerConfig(ICallerConfig callerConfig);

        Builder priority(int priority);

        IComponentProcessor build();
    }
}

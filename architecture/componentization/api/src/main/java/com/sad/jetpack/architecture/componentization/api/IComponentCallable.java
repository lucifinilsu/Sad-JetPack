package com.sad.jetpack.architecture.componentization.api;

public interface IComponentCallable extends ISortable{

    String componentId();

    IComponent component();

    ICallerConfig callerConfig();

    IComponentCallListener listener();

    void call(IRequest request);

    Builder toBuilder();

    interface Builder{

        Builder callerConfig(ICallerConfig config);

        Builder listener(IComponentCallListener listener);

        Builder componentId(String id);

        IComponentCallable build();
    }
}

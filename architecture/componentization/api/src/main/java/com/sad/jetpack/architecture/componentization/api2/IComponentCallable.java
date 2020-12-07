package com.sad.jetpack.architecture.componentization.api2;

public interface IComponentCallable {

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

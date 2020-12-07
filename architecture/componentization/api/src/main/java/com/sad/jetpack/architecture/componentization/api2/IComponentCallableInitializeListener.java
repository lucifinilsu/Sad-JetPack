package com.sad.jetpack.architecture.componentization.api2;


public interface IComponentCallableInitializeListener {

    void onComponentClassFound(ComponentClassInfo info);

    IComponentCallable onComponentCallableInstanceObtained(IComponentCallable componentCallable);

    Object onObjectInstanceObtained(String curl, Object object);

    void onTraverseCRMException(Throwable e);
}

package com.sad.jetpack.architecture.componentization.api2;

public interface IComponentInitializeListener {

    void onComponentClassFound(ComponentClassInfo info);

    IComponent onComponentInstanceObtained(String curl, IComponent component);

    Object onObjectInstanceObtained(String curl, Object object);

    void onTraverseCRMException(Throwable e);
}

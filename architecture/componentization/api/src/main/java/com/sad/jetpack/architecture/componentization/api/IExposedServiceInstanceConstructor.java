package com.sad.jetpack.architecture.componentization.api;

public interface IExposedServiceInstanceConstructor {

    IExposedServiceInstanceConstructor constructorClass(Class... classes);

    IExposedServiceInstanceConstructor constructorParameters(Object... objects);

    <T> T instance();

}

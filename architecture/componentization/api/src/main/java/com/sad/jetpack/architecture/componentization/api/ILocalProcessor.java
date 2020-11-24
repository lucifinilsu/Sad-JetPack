package com.sad.jetpack.architecture.componentization.api;

import java.util.LinkedHashMap;

public interface ILocalProcessor<I extends ILocalProcessor<I>> extends IComponentProcessor<I> {

    LinkedHashMap<Object,String> extraObjectInstances();

    LinkedHashMap<IExposedService,String> exposedServiceInstance();

}

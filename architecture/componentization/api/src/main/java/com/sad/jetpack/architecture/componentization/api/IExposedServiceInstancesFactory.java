package com.sad.jetpack.architecture.componentization.api;

import java.util.LinkedHashMap;

public interface IExposedServiceInstancesFactory {

    LinkedHashMap<Object,String> extraObjectInstances();

    LinkedHashMap<IExposedService,String> exposedServiceInstances();
}

package com.sad.jetpack.architecture.componentization.api;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public interface IExposedServiceGroupRepository {

    LinkedHashMap<String,Class> serviceClassList();

    IExposedServiceInstanceConstructor serviceInstance(String ermPath) throws Exception;

    IExposedServiceInstanceConstructor serviceInstanceFirst() throws Exception;

    Creator creator();

    interface Creator{

        Creator serviceFactory(IExposedServiceClassFactory serviceFactory);

        IExposedServiceGroupRepository create();
    }

}

package com.sad.jetpack.architecture.componentization.api2;


import java.util.Map;

public interface InstancesRepositoryFactory {

    InstancesRepository from(String url,IConstructor allConstructor, Map<String, IConstructor> constructors, IComponentCallableInitializeListener listener);

}

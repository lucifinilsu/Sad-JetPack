package com.sad.jetpack.architecture.componentization.api2;

import java.util.Map;

public interface IComponentRepositoryFactory {

    IComponentRepository from(String url,IConstructor allConstructor,Map<String,IConstructor> constructors, IComponentInitializeListener listener);

}

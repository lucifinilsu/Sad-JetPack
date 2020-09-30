package com.sad.jetpack.architecture.componentization.api2;

import java.util.Map;

public interface IComponentRepositoryFactory {

    IComponentRepository from(String url, Map<String,IConstructor> constructors);

}

package com.sad.jetpack.architecture.componentization.api;

public interface IExposedServiceClassFactory {

    Class getServiceClass(ExposedServiceRelationMappingElement element);

}

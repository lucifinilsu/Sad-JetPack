package com.sad.jetpack.architecture.componentization.api;

public interface IExposedServiceManager {

    IExposedServiceManagerAsync asyncScanERM();

    IExposedServiceManager entityGroupFactory(IExposedServiceEntityGroupFactory entityGroupFactory);

    IExposedServiceManager entityFoundListener(IExposedServiceEntityGroupFactory.OnExposedServiceRelationMappingEntityFoundListener onExposedServiceRelationMappingEntityFoundListener);

    ExposedServiceRelationMappingEntity getFirstEntity(String url) throws Exception;

    IExposedServiceInstanceConstructor getFirst(String url)throws Exception;

    ICluster cluster(String url);

}

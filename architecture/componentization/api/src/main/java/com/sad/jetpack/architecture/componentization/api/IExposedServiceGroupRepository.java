package com.sad.jetpack.architecture.componentization.api;

import androidx.work.ListenableWorker;
import androidx.work.WorkRequest;
import androidx.work.Worker;

import java.util.LinkedHashMap;

public interface IExposedServiceGroupRepository {

    LinkedHashMap<String, ExposedServiceRelationMappingEntity> entityGroup();

    //IExposedServiceGroupRepository serviceClassFactory(IExposedServiceClassFactory serviceClassFactory);

    LinkedHashMap<String,Class> serviceClassList();

    IExposedServiceInstanceConstructor serviceInstance(String ermPath) throws Exception;

    IExposedServiceInstanceConstructor serviceInstanceFirst() throws Exception;

    LinkedHashMap<String, Class<ListenableWorker>> workerClassGroup();

    //LinkedHashMap<String, WorkRequest> workRequestGroup();

    IPerformer commit();


}

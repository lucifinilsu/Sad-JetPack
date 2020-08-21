package com.sad.jetpack.architecture.componentization.api;

import androidx.work.ListenableWorker;
import androidx.work.WorkRequest;
import androidx.work.Worker;

import java.util.LinkedHashMap;

public interface IExposedServiceGroupRepository {

    LinkedHashMap<String, ExposedServiceRelationMappingEntity> entityGroup();

    LinkedHashMap<String, Class<ListenableWorker>> workerClassGroup();

    String orgUrl();

}

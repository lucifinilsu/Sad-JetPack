package com.sad.jetpack.architecture.componentization.api.impl;

import com.sad.jetpack.architecture.componentization.api.ExposedServiceInstanceStorageManager;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceGroupRepository;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceInstanceConstructorParameters;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceInstancesFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DefaultInstanceUseAsPoster implements IExposedServiceInstancesFactory {
    private IExposedServiceGroupRepository repository=null;
    private LinkedHashMap<IExposedService,String> extraExposedService=new LinkedHashMap<>();
    private List<String> excludeUrls=new ArrayList<>();

    public DefaultInstanceUseAsPoster(IExposedServiceGroupRepository repository, LinkedHashMap<IExposedService, String> extraExposedService, List<String> excludeUrls) {
        this.repository = repository;
        this.extraExposedService = extraExposedService;
        this.excludeUrls = excludeUrls;
    }

    @Override
    public LinkedHashMap<Object, String> extraObjectInstances() {
        return null;
    }

    @Override
    public LinkedHashMap<IExposedService, String> exposedServiceInstances() {
        LinkedHashMap<IExposedService,String> esNoSort= ExposedServiceInstanceStorageManager.getExposedServices(repository.orgUrl(),excludeUrls.toArray(new String[excludeUrls.size()]));
        LinkedHashMap<IExposedService,String> es=new LinkedHashMap<>();
        List<IExposedService> eList=new ArrayList<>(esNoSort.keySet());
        List<String> uList=new ArrayList<>(esNoSort.values());
        eList.addAll(extraExposedService.keySet());
        uList.addAll(extraExposedService.values());
        List<IExposedService> temp=new ArrayList<>(eList);
        if (!temp.isEmpty()){
            Collections.sort(temp);
        }
        for (IExposedService ee:temp
        ) {
            int index=eList.indexOf(ee);
            String orgUrl=uList.get(index);
            es.put(ee,orgUrl);
        }
        return es;
    }
}

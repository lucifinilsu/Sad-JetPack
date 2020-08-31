package com.sad.jetpack.architecture.componentization.api.internal;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.sad.jetpack.architecture.componentization.api.ExposedServiceRelationMappingEntity;
import com.sad.jetpack.architecture.componentization.api.ICluster;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceGroupRepository;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceInstanceConstructorParameters;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceInstancesFactory;
import com.sad.jetpack.architecture.componentization.api.IProcessor;
import com.sad.jetpack.architecture.componentization.api.MapTraverseUtils;
import com.sad.jetpack.architecture.componentization.api.impl.DefaultInstanceUseAsCaller;
import com.sad.jetpack.architecture.componentization.api.impl.DefaultInstanceUseAsPoster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InternalCluster implements ICluster {

    private IExposedServiceGroupRepository repository=null;
    private Map<String, List<IExposedServiceInstanceConstructorParameters>> constructorParameters=new LinkedHashMap<>();
    private List<String> excludeUrls=new ArrayList<>();
    private LinkedHashMap<IExposedService,String> extraExposedService=new LinkedHashMap<>();

    private IExposedServiceInstancesFactory instancesFactory;

    public InternalCluster(IExposedServiceGroupRepository repository) {
        this.repository = repository;
    }

    @Override
    public IExposedServiceGroupRepository repository() {
        return this.repository;
    }

    @Override
    public ICluster addInstanceConstructorParameters(@NonNull String orgUrl, @NonNull IExposedServiceInstanceConstructorParameters parameters){
        if (!TextUtils.isEmpty(orgUrl) && parameters!=null){
            List<IExposedServiceInstanceConstructorParameters> p=constructorParameters.get(orgUrl);
            if (p==null){
                p=new ArrayList<>();
            }
            p.add(parameters);
            constructorParameters.put(orgUrl,p);
        }
        return this;
    }
    @Override
    public ICluster addInstanceConstructorParameters(@NonNull IExposedServiceInstanceConstructorParameters parameters){
        LinkedHashMap<String, ExposedServiceRelationMappingEntity> e_map=repository.entityGroup();
        MapTraverseUtils.traverseGroup(e_map, new MapTraverseUtils.ITraverseAction<String, ExposedServiceRelationMappingEntity>() {
            @Override
            public void onTraversed(String s, ExposedServiceRelationMappingEntity entity) {
                addInstanceConstructorParameters(s,parameters);
            }
        });
        return this;
    }

    @Override
    public ICluster exclude(String... e_url) {
        for (String u:e_url
             ) {
            constructorParameters.remove(u);
        }
        excludeUrls.addAll(Arrays.asList(e_url));
        return this;
    }
    public ICluster addExtraExposedServiceInstance(IExposedService exposedService,String orgUrl){
        extraExposedService.put(exposedService,orgUrl);
        return this;
    }

    @Override
    public IProcessor call() {
        return proceedAs(new DefaultInstanceUseAsCaller(repository,extraExposedService,excludeUrls,constructorParameters));
    }

    @Override
    public IProcessor post() {
        return proceedAs(new DefaultInstanceUseAsPoster(repository,extraExposedService,excludeUrls));
    }



    @Override
    public IProcessor proceedAs(IExposedServiceInstancesFactory factory) {
        this.instancesFactory=factory;
        InternalProcessor processor=new InternalProcessor();
        processor.setExposedServices(this.instancesFactory.exposedServiceInstances());
        processor.setExtraObjects(this.instancesFactory.extraObjectInstances());
        return processor;
    }


}

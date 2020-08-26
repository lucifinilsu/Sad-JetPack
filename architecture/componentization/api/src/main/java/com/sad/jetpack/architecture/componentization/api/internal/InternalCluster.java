package com.sad.jetpack.architecture.componentization.api.internal;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.sad.jetpack.architecture.componentization.api.ExposedServiceInstanceStorageManager;
import com.sad.jetpack.architecture.componentization.api.ExposedServiceRelationMappingEntity;
import com.sad.jetpack.architecture.componentization.api.ICluster;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceGroupRepository;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceInstanceConstructorParameters;
import com.sad.jetpack.architecture.componentization.api.IProcessor;
import com.sad.jetpack.architecture.componentization.api.MapTraverseUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InternalCluster implements ICluster {

    private IExposedServiceGroupRepository repository=null;
    private Map<String, List<IExposedServiceInstanceConstructorParameters>> constructorParameters=new LinkedHashMap<>();
    private List<String> excludeUrls=new ArrayList<>();
    private List<IExposedService> extraExposedService=new ArrayList<>();
    private int processMode=ICluster.CALL_MODE_SEQUENCE;

    public InternalCluster(IExposedServiceGroupRepository repository) {
        this.repository = repository;
    }

    @Override
    public IExposedServiceGroupRepository repository() {
        return this.repository;
    }

    @Override
    public ICluster addInstanceConstructorParameters(@NonNull String e_url, @NonNull IExposedServiceInstanceConstructorParameters parameters){
        if (!TextUtils.isEmpty(e_url) && parameters!=null){
            List<IExposedServiceInstanceConstructorParameters> p=constructorParameters.get(e_url);
            if (p==null){
                p=new ArrayList<>();
            }
            p.add(parameters);
            constructorParameters.put(e_url,p);
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
    public ICluster addExtraExposedServiceInstance(IExposedService exposedService){
        extraExposedService.add(exposedService);
        return this;
    }

    @Override
    public IProcessor call() {
        InternalProcessor processor=new InternalProcessor(processMode);
        //首先遍历生成实例并排序
        LinkedHashMap<String, ExposedServiceRelationMappingEntity> e_map=repository.entityGroup();
        ArrayList<IExposedService> exposedServices=new ArrayList<>();
        ArrayList extraObjectInstances=new ArrayList();
        MapTraverseUtils.traverseGroup(e_map, new MapTraverseUtils.ITraverseAction<String, ExposedServiceRelationMappingEntity>() {
            @Override
            public void onTraversed(String s, ExposedServiceRelationMappingEntity entity) {
                if (!excludeUrls.contains(s)){
                    String clsName=entity.getElement().getClassName();
                    Class cls=null;
                    try {
                        cls=Class.forName(clsName);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    if (cls!=null){
                        InternalExposedServiceInstanceConstructor constructor=new InternalExposedServiceInstanceConstructor(cls);
                        List<IExposedServiceInstanceConstructorParameters> ps=constructorParameters.get(s);
                        if (ps!=null && !ps.isEmpty()){
                            for (IExposedServiceInstanceConstructorParameters p:ps
                            ) {
                                constructor.constructorClass(p.constructorClass()).constructorParameters(p.constructorParameters());
                                Object o=constructor.instance();
                                if (o!=null){
                                    if (o instanceof IExposedService){
                                        exposedServices.add((IExposedService) o);
                                    }
                                    else {
                                        extraObjectInstances.add(o);
                                    }
                                }

                            }
                        }
                        else {
                            Object o=constructor.instance();
                            if (o!=null){
                                if (o instanceof IExposedService){
                                    exposedServices.add((IExposedService) o);
                                }
                                else {
                                    extraObjectInstances.add(o);
                                }
                            }
                        }
                    }
                }
            }
        });
        exposedServices.addAll(extraExposedService);
        if (!exposedServices.isEmpty()){
            Collections.sort(exposedServices);
        }
        processor.setExposedServices(exposedServices);
        processor.setExtraObjects(extraObjectInstances);
        return processor;
    }

    @Override
    public IProcessor post() {
        InternalProcessor processor=new InternalProcessor(processMode);
        List<IExposedService> exposedServices= ExposedServiceInstanceStorageManager.getExposedServices(repository.orgUrl(),excludeUrls.toArray(new String[excludeUrls.size()]));
        exposedServices.addAll(extraExposedService);
        if (!exposedServices.isEmpty()){
            Collections.sort(exposedServices);
        }
        processor.setExposedServices(exposedServices);
        return processor;
    }

    @Override
    public ICluster processMode(int processMode) {
        this.processMode=processMode;
        return this;
    }


}

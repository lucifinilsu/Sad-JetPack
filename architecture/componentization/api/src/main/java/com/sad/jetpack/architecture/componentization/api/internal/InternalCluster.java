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
    private LinkedHashMap<IExposedService,String> extraExposedService=new LinkedHashMap<>();
    private int processMode=ICluster.CALL_MODE_SEQUENCE;

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
        InternalProcessor processor=new InternalProcessor(processMode);
        //首先遍历生成实例并排序
        LinkedHashMap<String, ExposedServiceRelationMappingEntity> e_map=repository.entityGroup();
        List<IExposedService> exposedServices=new ArrayList<>();
        List extraObjectInstances=new ArrayList();
        List<String> e_urls=new ArrayList<>();
        List<String> o_urls=new ArrayList<>();
        LinkedHashMap<IExposedService,String> es=new LinkedHashMap<>();
        LinkedHashMap<Object,String> os=new LinkedHashMap<>();
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
                                        e_urls.add(s);
                                    }
                                    else {
                                        extraObjectInstances.add(o);
                                        o_urls.add(s);
                                    }
                                }

                            }
                        }
                        else {
                            Object o=constructor.instance();
                            if (o!=null){
                                if (o instanceof IExposedService){
                                    exposedServices.add((IExposedService) o);
                                    e_urls.add(s);
                                }
                                else {
                                    extraObjectInstances.add(o);
                                    o_urls.add(s);
                                }
                            }
                        }
                    }
                }
            }
        });
        exposedServices.addAll(extraExposedService.keySet());
        e_urls.addAll(extraExposedService.values());
        List<IExposedService> temp=new ArrayList<>(exposedServices);
        if (!temp.isEmpty()){
            Collections.sort(temp);
        }
        for (IExposedService e:temp
             ) {
            es.put(e,e_urls.get(exposedServices.indexOf(e)));
        }
        for (Object o:extraObjectInstances){
            os.put(o,o_urls.get(extraObjectInstances.indexOf(o)));
        }
        processor.setExposedServices(es);
        processor.setExtraObjects(os);
        return processor;
    }

    @Override
    public IProcessor post() {
        InternalProcessor processor=new InternalProcessor(processMode);
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
        processor.setExposedServices(es);
        return processor;
    }

    @Override
    public ICluster processMode(int processMode) {
        this.processMode=processMode;
        return this;
    }


}

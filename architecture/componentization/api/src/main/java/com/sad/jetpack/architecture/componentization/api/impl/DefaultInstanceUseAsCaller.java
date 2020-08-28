package com.sad.jetpack.architecture.componentization.api.impl;

import com.sad.jetpack.architecture.componentization.api.ExposedServiceRelationMappingEntity;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceGroupRepository;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceInstanceConstructorParameters;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceInstancesFactory;
import com.sad.jetpack.architecture.componentization.api.MapTraverseUtils;
import com.sad.jetpack.architecture.componentization.api.internal.InternalExposedServiceInstanceConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DefaultInstanceUseAsCaller implements IExposedServiceInstancesFactory {

    private IExposedServiceGroupRepository repository=null;
    private LinkedHashMap<IExposedService,String> extraExposedService=new LinkedHashMap<>();
    private List<String> excludeUrls=new ArrayList<>();
    private Map<String, List<IExposedServiceInstanceConstructorParameters>> constructorParameters=new LinkedHashMap<>();

    public DefaultInstanceUseAsCaller(IExposedServiceGroupRepository repository, LinkedHashMap<IExposedService, String> extraExposedService, List<String> excludeUrls, Map<String, List<IExposedServiceInstanceConstructorParameters>> constructorParameters) {
        this.repository = repository;
        this.extraExposedService = extraExposedService;
        this.excludeUrls = excludeUrls;
        this.constructorParameters = constructorParameters;
        create();
    }

    private void create(){
        //首先遍历生成实例并排序
        LinkedHashMap<String, ExposedServiceRelationMappingEntity> e_map=repository.entityGroup();
        List<IExposedService> exposedServices=new ArrayList<>();
        List extraObjectInstances=new ArrayList();
        List<String> e_urls=new ArrayList<>();
        List<String> o_urls=new ArrayList<>();

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
    }

    @Override
    public LinkedHashMap<Object, String> extraObjectInstances() {
        return os;
    }
    LinkedHashMap<IExposedService,String> es=new LinkedHashMap<>();
    LinkedHashMap<Object,String> os=new LinkedHashMap<>();
    @Override
    public LinkedHashMap<IExposedService, String> exposedServiceInstances() {

        return es;
    }
}

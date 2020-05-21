package com.sad.jetpack.architecture.componentization.api.impl;
import com.sad.jetpack.architecture.componentization.api.ExposdServiceRelationMappingElement;
import com.sad.jetpack.architecture.componentization.api.ExposdServiceRelationMappingEntity;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceClassFactory;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceEntityHandler;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceGroupRepository;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceInstanceConstructor;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultExposedServiceGroupRepository implements IExposedServiceGroupRepository,IExposedServiceGroupRepository.Creator{
    private IExposedServiceEntityHandler serviceEntityHandler;
    private IExposedServiceClassFactory serviceFactory;
    public DefaultExposedServiceGroupRepository(IExposedServiceEntityHandler serviceEntityHandler){
        this.serviceEntityHandler=serviceEntityHandler;
    }

    @Override
    public LinkedHashMap<String, Class> serviceClassList() {
        if (serviceEntityHandler!=null){
            LinkedHashMap<String,ExposdServiceRelationMappingEntity> entityGroup = serviceEntityHandler.entityGroup();
            if (!ObjectUtils.isEmpty(entityGroup)){
                LinkedHashMap<String, Class> classLinkedHashMap=new LinkedHashMap<>();
                Iterator<Map.Entry<String,ExposdServiceRelationMappingEntity>> iterator=entityGroup.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<String,ExposdServiceRelationMappingEntity> entityEntry=iterator.next();
                    String ermPath=entityEntry.getKey();
                    ExposdServiceRelationMappingEntity entity=entityEntry.getValue();
                    if (entity!=null){
                        ExposdServiceRelationMappingElement element=entity.getElement();
                        if (element!=null){
                            if (serviceFactory==null){
                                serviceFactory=new DefaultExposedServiceClassFactory();
                            }
                            Class cls=serviceFactory.getServiceClass(element);
                            if (cls!=null){
                                classLinkedHashMap.put(ermPath,cls);
                            }
                        }
                    }
                }
                return classLinkedHashMap;
            }

        }
        return new LinkedHashMap<>();
    }

    @Override
    public IExposedServiceInstanceConstructor serviceInstance(String ermPath) throws Exception{
        Class cls=serviceClassList().get(ermPath);
        if (cls!=null){
            return new DefaultExposedServiceInstanceConstructor(cls);
        }
        else {
            throw new Exception("serviceClass whose ermPth is '"+ermPath+"' is null !!!");
        }
    }

    @Override
    public IExposedServiceInstanceConstructor serviceInstanceFirst() throws Exception{
        if (serviceClassList().entrySet().iterator().hasNext()){
            Map.Entry<String,Class> entityEntry=serviceClassList().entrySet().iterator().next();
            String ermPath=entityEntry.getKey();
            Class cls=entityEntry.getValue();
            if (cls!=null){
                return new DefaultExposedServiceInstanceConstructor(cls);
            }
            else {
                throw new Exception("serviceClass whose ermPth is '"+ermPath+"' is null !!!");
            }
        }
        else {
            throw new Exception("serviceClassList is empty !!!");
        }
    }

    @Override
    public Creator creator() {
        return this;
    }

    @Override
    public Creator serviceFactory(IExposedServiceClassFactory serviceFactory) {
        this.serviceFactory=serviceFactory;
        return this;
    }

    @Override
    public IExposedServiceGroupRepository create() {
        return this;
    }
}

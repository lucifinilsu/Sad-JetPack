package com.sad.jetpack.architecture.componentization.api;

import android.util.Log;

import com.sad.jetpack.architecture.componentization.api.impl.DefaultExposedServiceClassFactory;
import com.sad.jetpack.architecture.componentization.api.impl.DefaultExposedServiceEntityGroupFactory;
import com.sad.jetpack.architecture.componentization.api.internal.InternalExposedServiceGroupRepository;

import java.util.LinkedHashMap;

public class ExposedServiceManager {

    private ExposedServiceManager(){}

    public static ExposedServiceManager newInstance(){
        return new ExposedServiceManager();
    }

    private IExposedServiceEntityGroupFactory entityGroupFactory;
    private IExposedServiceClassFactory serviceClassFactory;

    public ExposedServiceManager entityGroupFactory(IExposedServiceEntityGroupFactory entityGroupFactory){
        this.entityGroupFactory=entityGroupFactory;
        return this;
    }
    public ExposedServiceManager serviceClassFactory(IExposedServiceClassFactory serviceClassFactory){
        this.serviceClassFactory=serviceClassFactory;
        return this;
    }
    public IExposedServiceGroupRepository repository(String url){
        if (entityGroupFactory==null){
            entityGroupFactory=new DefaultExposedServiceEntityGroupFactory(InternalContextHolder.get().getContext());
        }
        if (serviceClassFactory==null){
            serviceClassFactory=new DefaultExposedServiceClassFactory();
        }
        LinkedHashMap<String, ExposedServiceRelationMappingEntity> entityGroup=entityGroupFactory.getEntityGroup(url);
        Log.e("sad-jetpack",">>>>开始生成Repository："+url+",准备实体组："+entityGroup);
        IExposedServiceGroupRepository repository=new InternalExposedServiceGroupRepository(InternalContextHolder.get().getContext(),serviceClassFactory,entityGroup);
        return repository;
    }
    public static IExposedServiceInstanceConstructor exposedServiceFirst(String url) throws Exception{
        return ExposedServiceManager.newInstance()
                .repository(url)
                .serviceInstanceFirst();
    }

}

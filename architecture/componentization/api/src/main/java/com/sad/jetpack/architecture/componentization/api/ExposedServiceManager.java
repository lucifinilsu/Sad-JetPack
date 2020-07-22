package com.sad.jetpack.architecture.componentization.api;

import android.util.Log;

import com.sad.jetpack.architecture.componentization.api.impl.DefaultExposedServiceClassFactory;
import com.sad.jetpack.architecture.componentization.api.impl.DefaultExposedServiceEntityGroupFactory;
import com.sad.jetpack.architecture.componentization.api.internal.InternalExposedServiceGroupRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ExposedServiceManager {

    private ExposedServiceManager(){}

    public static ExposedServiceManager newInstance(){
        return new ExposedServiceManager();
    }

    private IExposedServiceEntityGroupFactory entityGroupFactory;
    private IExposedServiceClassFactory serviceClassFactory;
    private List<Interceptor> commonInterceptors=new ArrayList<>();

    public ExposedServiceManager entityGroupFactory(IExposedServiceEntityGroupFactory entityGroupFactory){
        this.entityGroupFactory=entityGroupFactory;
        return this;
    }
    public ExposedServiceManager serviceClassFactory(IExposedServiceClassFactory serviceClassFactory){
        this.serviceClassFactory=serviceClassFactory;
        return this;
    }


    public IExposedServiceGroupRepository get(String url){
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
    public static IExposedServiceInstanceConstructor getFirst(String url) throws Exception{
        return ExposedServiceManager.newInstance()
                .get(url)
                .serviceInstanceFirst();
    }

    /**
     * 爱尔 长春 凯莱英 恒瑞 。普利 药明康德 迈瑞 。泰格 。我武 。华海
     */

}

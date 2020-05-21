package com.sad.jetpack.architecture.componentization.api;

import com.sad.jetpack.architecture.componentization.api.impl.DefaultExposedServiceEntityGroupFactory;
import com.sad.jetpack.architecture.componentization.api.impl.DefaultExposedServiceEntityHandler;

public class ESRManager {

    private ESRManager(){}

    public static ESRManager get(){
        return new ESRManager();
    }


    public IExposedServiceEntityHandler.Creator handle(String url){
        return handle(url, new DefaultExposedServiceEntityGroupFactory(InternalContextHolder.get().getContext()));
    }

    public IExposedServiceEntityHandler.Creator handle(String url,IExposedServiceEntityGroupFactory entityGroupFactory){
        return new DefaultExposedServiceEntityHandler()
                .creator()
                .url(url)
                .entityGroupFactory(entityGroupFactory)
                ;
    }
    public static IExposedServiceInstanceConstructor exposedServiceFirst(String url) throws Exception{
        return ESRManager.get()
                .handle(url)
                .create()
                .repository()
                .serviceInstanceFirst();
    }

}

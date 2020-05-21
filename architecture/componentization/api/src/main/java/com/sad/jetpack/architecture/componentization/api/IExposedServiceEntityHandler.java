package com.sad.jetpack.architecture.componentization.api;

import java.util.LinkedHashMap;

public interface IExposedServiceEntityHandler {

    LinkedHashMap<String,ExposdServiceRelationMappingEntity> entityGroup();

    IExposedServiceGroupRepository repository();

    default ExposdServiceRelationMappingEntity getEntityFirst(){
        LinkedHashMap<String, ExposdServiceRelationMappingEntity> map= entityGroup();
        if (map.size()>0){
            ExposdServiceRelationMappingEntity entity= map.values().toArray(new ExposdServiceRelationMappingEntity[map.size()])[0];
            return entity;
        }
        return null;
    }

    IExposedServiceEntityHandler appendExternalEntity(ExposdServiceRelationMappingEntity entity);

    Creator creator();

    interface Creator{

        Creator url(String url);

        Creator entityGroupFactory(IExposedServiceEntityGroupFactory entityGroupFactory);

        IExposedServiceEntityHandler create();

    }

}

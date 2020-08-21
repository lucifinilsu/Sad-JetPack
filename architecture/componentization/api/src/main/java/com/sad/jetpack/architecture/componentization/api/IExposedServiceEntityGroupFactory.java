package com.sad.jetpack.architecture.componentization.api;

import java.util.LinkedHashMap;

public interface IExposedServiceEntityGroupFactory {

    LinkedHashMap<String, ExposedServiceRelationMappingEntity> getEntityGroupByUrl(String url,OnExposedServiceRelationMappingEntityFoundListener entityFoundListener);

    interface OnExposedServiceRelationMappingEntityFoundListener{

        boolean onEntityFound(ExposedServiceRelationMappingEntity entity,boolean isFirst);

    }

}

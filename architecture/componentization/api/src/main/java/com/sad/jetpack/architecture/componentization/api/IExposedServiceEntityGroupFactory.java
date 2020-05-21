package com.sad.jetpack.architecture.componentization.api;

import java.util.LinkedHashMap;

public interface IExposedServiceEntityGroupFactory {

    LinkedHashMap<String,ExposdServiceRelationMappingEntity> getEntityGroup(String url);



}

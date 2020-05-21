package com.sad.jetpack.architecture.componentization.api.impl;

import com.sad.jetpack.architecture.componentization.api.ExposdServiceRelationMappingEntity;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceEntityGroupFactory;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceEntityHandler;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceGroupRepository;

import java.util.LinkedHashMap;

public class DefaultExposedServiceEntityHandler implements IExposedServiceEntityHandler, IExposedServiceEntityHandler.Creator {
    private LinkedHashMap<String, ExposdServiceRelationMappingEntity> entityGroup=new LinkedHashMap<>();
    private String url="";
    private IExposedServiceEntityGroupFactory entityGroupFactory;
    @Override
    public LinkedHashMap<String, ExposdServiceRelationMappingEntity> entityGroup() {
        return this.entityGroup;
    }

    @Override
    public IExposedServiceGroupRepository repository() {
        return new DefaultExposedServiceGroupRepository(this);
    }

    @Override
    public IExposedServiceEntityHandler appendExternalEntity(ExposdServiceRelationMappingEntity entity) {
        this.entityGroup.put(entity.getPath(),entity);
        return this;
    }

    @Override
    public Creator creator() {
        return this;
    }

    @Override
    public Creator url(String url) {
        this.url=url;
        return this;
    }

    @Override
    public Creator entityGroupFactory(IExposedServiceEntityGroupFactory entityGroupFactory) {
        this.entityGroupFactory=entityGroupFactory;
        return this;
    }

    @Override
    public IExposedServiceEntityHandler create() {
        this.entityGroup=this.entityGroupFactory.getEntityGroup(url);
        return this;
    }

    /*private IExposedServiceGroupRepository performer=new DefaultExposedServiceGroupRepository();
    private LinkedHashMap<String,ExposdServiceRelationMappingEntity> entityGroup=new LinkedHashMap<>();
    @Override
    public IExposedServiceGroupRepository performer() {
        return this.performer;
    }

    @Override
    public LinkedHashMap<String, ExposdServiceRelationMappingEntity> entityGroup() {
        return this.entityGroup;
    }

    @Override
    public void execute() {
        if (performer!=null){
            performer.onPerform(this.entityGroup);
        }
    }

    @Override
    public Creator creator() {
        return this;
    }

    @Override
    public Creator performer(IExposedServiceGroupRepository performer) {
        this.performer=performer;
        return this;
    }

    @Override
    public IExposedServiceEntityHandler create() {
        return this;
    }

    @Override
    public Creator entityGroup(LinkedHashMap<String, ExposdServiceRelationMappingEntity> group) {
        this.entityGroup=group;
        return this;
    }

    @Override
    public Creator appendEntity(String ermPath, ExposdServiceRelationMappingEntity entity) {
        this.entityGroup.put(ermPath,entity);
        return this;
    }*/
}

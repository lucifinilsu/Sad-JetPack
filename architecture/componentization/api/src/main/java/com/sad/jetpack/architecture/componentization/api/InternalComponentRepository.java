package com.sad.jetpack.architecture.componentization.api;

import java.util.LinkedHashMap;

final class InternalComponentRepository implements IComponentRepository {
    private LinkedHashMap<Object, String> objectInstances =new LinkedHashMap<>();
    private LinkedHashMap<IComponent, String> componentInstances =new LinkedHashMap<>();
    private String url="";

    public InternalComponentRepository(String url,LinkedHashMap<Object, String> objectInstances, LinkedHashMap<IComponent, String> componentInstances) {
        this.objectInstances = objectInstances;
        this.componentInstances = componentInstances;
        this.url=url;
    }

    public InternalComponentRepository(String url){
        this.url=url;
    }

    public void setComponentInstances(LinkedHashMap<IComponent, String> componentInstances) {
        this.componentInstances = componentInstances;
    }

    public void setObjectInstances(LinkedHashMap<Object, String> objectInstances) {
        this.objectInstances = objectInstances;
    }

    @Override
    public String url() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public LinkedHashMap<Object, String> objectInstances() {
        return this.objectInstances;
    }

    @Override
    public LinkedHashMap<IComponent, String> componentInstances() {
        return this.componentInstances;
    }
}

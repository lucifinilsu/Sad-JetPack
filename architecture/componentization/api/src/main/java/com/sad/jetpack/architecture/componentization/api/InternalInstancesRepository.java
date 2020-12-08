package com.sad.jetpack.architecture.componentization.api;


import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

final class InternalInstancesRepository implements InstancesRepository {
    private LinkedHashMap<Object, String> objectInstances =new LinkedHashMap<>();
    private List<IComponentCallable> componentInstances =new LinkedList<>();
    private String url="";

    public InternalInstancesRepository(String url, LinkedHashMap<Object, String> objectInstances,List<IComponentCallable> componentInstances) {
        this.objectInstances = objectInstances;
        this.componentInstances = componentInstances;
        this.url=url;
    }

    public InternalInstancesRepository(String url){
        this.url=url;
    }

    public void setComponentInstances(List<IComponentCallable> componentInstances) {
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
    public List<IComponentCallable> componentCallableInstances() {
        return this.componentInstances;
    }
}

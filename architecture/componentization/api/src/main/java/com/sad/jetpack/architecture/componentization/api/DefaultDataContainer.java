package com.sad.jetpack.architecture.componentization.api;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDataContainer implements IDataContainer {
    private static final long serialVersionUID = -4329451829604650322L;
    private Map map;
    public static IDataContainer newIntance(){
        return new DefaultDataContainer();
    }
    private DefaultDataContainer(){
        this.map=new LinkedHashMap();
    }
    @Override
    public Map getMap() {
        return this.map;
    }

    @Override
    public <T> T get(Object key) {
        Object o=map.get(key);
        if (o!=null){
            return (T) o;
        }
        return null;
    }

    @Override
    public IDataContainer put(Object key, Object value) {
        map.put(key,value);
        return this;
    }

    @Override
    public IDataContainer putAll(Map map) {
        map.putAll(map);
        return this;
    }

    @Override
    public IDataContainer putAll(IDataContainer dataContainer) {
        map.putAll(dataContainer.getMap());
        return this;
    }
}

package com.sad.jetpack.architecture.componentization.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDataContainer implements IDataContainer {
    private static final long serialVersionUID = -4329451829604650322L;
    private Map map;
    public static IDataContainer newIntance(){
        return new DefaultDataContainer();
    }
    private DefaultDataContainer(){
        this.map=new ConcurrentHashMap();
    }
    @Override
    public Map getMap() {
        return this.map;
    }
}

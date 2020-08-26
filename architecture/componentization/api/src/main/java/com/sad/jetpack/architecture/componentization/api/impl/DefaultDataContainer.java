package com.sad.jetpack.architecture.componentization.api.impl;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

public class DefaultDataContainer {

    private ConcurrentLinkedHashMap<String,Object> dataMap=new ConcurrentLinkedHashMap.Builder<String,Object>().build();
    private DefaultDataContainer(){}
    public static DefaultDataContainer newInstance(){
        return new DefaultDataContainer();
    }
    public DefaultDataContainer add(String url,Object data){
        dataMap.put(url,data);
        return this;
    }
    public <T> T get(String url){
        Object o=dataMap.get(url);
        if (o!=null){
            return (T) o;
        }
        return null;
    }

}

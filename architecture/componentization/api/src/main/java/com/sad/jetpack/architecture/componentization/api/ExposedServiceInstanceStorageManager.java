package com.sad.jetpack.architecture.componentization.api;

import android.util.Log;

import com.sad.jetpack.architecture.componentization.annotation.EncryptUtil;
import com.sad.jetpack.architecture.componentization.annotation.ExposedService;
import com.sad.jetpack.architecture.componentization.annotation.ValidUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ExposedServiceInstanceStorageManager {

    protected static final ConcurrentMap<String,List<IExposedService>> EXPOSEDSERVICE_INSTANCE_MAP = new ConcurrentHashMap<>();
    protected static final String PATH_KEY_SEPARATOR="sca#bas%Scbkabf@_sad_@&";

    public static List<IExposedService> getExposedServices(String url,String... exclude){
        List<String> ex=new ArrayList<String>(Arrays.asList(exclude));
        String name= url;//ValidUtils.encryptMD5ToString(url);
        List<IExposedService> list=new ArrayList<>();
        Iterator<Map.Entry<String,List<IExposedService>>> iterator=EXPOSEDSERVICE_INSTANCE_MAP.entrySet().iterator();
        Log.e("sad-jetpack","------------------->开始遍历组件名");
        while (iterator.hasNext()){
            Map.Entry<String,List<IExposedService>> entry=iterator.next();
            String k=entry.getKey();
            Log.e("sad-jetpack","------------------->遍历动态组件"+k);
            String[] n=k.split(PATH_KEY_SEPARATOR);
            if (n!=null && n.length>1){
                String ns=n[0];
                ns= EncryptUtil.getInstance().XORdecode(ns,"abc123");
                if (ns.startsWith(name) && ex.indexOf(ns)==-1){
                    Log.e("sad-jetpack","------------------->动态组件"+k+"符合需求，添加到结果集");
                    List<IExposedService> es=entry.getValue();
                    list.addAll(es);
                }
            }
        }
        return list;
    }

    public static void registerExposedServiceInstance(String name,IExposedService exposedService){
        List<IExposedService> exposedServices= EXPOSEDSERVICE_INSTANCE_MAP.get(name);
        if (exposedServices==null){
            exposedServices=new ArrayList<>();
        }
        exposedServices.add(exposedService);
        EXPOSEDSERVICE_INSTANCE_MAP.put(name,exposedServices);
    }
    public static void unregisterExposedServiceInstance(String name){
        List<IExposedService> components= EXPOSEDSERVICE_INSTANCE_MAP.get(name);
        if (components!=null){
            EXPOSEDSERVICE_INSTANCE_MAP.get(name).clear();
        }
        EXPOSEDSERVICE_INSTANCE_MAP.remove(name);
    }

}

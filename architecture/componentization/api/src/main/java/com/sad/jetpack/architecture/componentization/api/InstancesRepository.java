package com.sad.jetpack.architecture.componentization.api;

import java.util.LinkedHashMap;
import java.util.List;

public interface InstancesRepository {

    String url();

    LinkedHashMap<Object,String> objectInstances();

    List<IComponentCallable> componentCallableInstances();

    default <T> T firstInstance(Class<T> cls){
        Object o=firstObjectInstance();
        if (o!=null){
            return (T) o;
        }
        IComponentCallable c=firstComponentCallableInstance();
        if (c!=null){
            return (T) c;
        }
        return null;
    }

    default <T> T firstInstance(){
        Object o=firstObjectInstance();
        if (o!=null){
            return (T) o;
        }
        IComponentCallable c=firstComponentCallableInstance();
        if (c!=null){
            return (T) c;
        }
        return null;
    }

    default IComponentCallable firstComponentCallableInstance(){
        List<IComponentCallable> instances= componentCallableInstances();
        if (instances!=null && !instances.isEmpty()){
            instances.get(0);
        }
        return null;
    }
    default String firstComponentUrl(){
        IComponentCallable o= firstComponentCallableInstance();
        if (o!=null){
            return o.componentId();
        }
        return "";
    }

    default <T> T firstObjectInstance(){
        LinkedHashMap<Object,String> instances= objectInstances();
        if (instances!=null){
            Object[] os=instances.keySet().toArray(new Object[instances.size()]);
            if (os.length>0){
                return (T) os[0];
            }
        }
        return null;
    }
    default String firstObjectUrl(){
        Object o= firstObjectInstance();
        if (o!=null){
            return objectInstances().get(o);
        }
        return "";
    }

}

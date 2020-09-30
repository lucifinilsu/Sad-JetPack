package com.sad.jetpack.architecture.componentization.api2;

import java.util.LinkedHashMap;

public interface IComponentRepository {

    LinkedHashMap<Object,String> objectInstances();

    LinkedHashMap<IComponent,String> componentInstances();

    default IComponent firstComponentInstance(){
        LinkedHashMap<IComponent,String> instances= componentInstances();
        if (instances!=null){
            IComponent[] os=instances.keySet().toArray(new IComponent[instances.size()]);
            if (os.length>0){
                return os[0];
            }
        }
        return null;
    }
    default String firstComponentUrl(){
        IComponent o= firstComponentInstance();
        if (o!=null){
            return componentInstances().get(o);
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

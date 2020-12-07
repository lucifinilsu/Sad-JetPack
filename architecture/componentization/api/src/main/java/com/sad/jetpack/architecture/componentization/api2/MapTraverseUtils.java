package com.sad.jetpack.architecture.componentization.api2;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Iterator;
import java.util.Map;

public class MapTraverseUtils {

    public static interface ITraverseAction<K,V>{
        void onTraversed(K k, V v);
    }
    public static  <K,V> void traverseGroup(Map<K,V> map, ITraverseAction<K,V>... actions){
        if (!ObjectUtils.isEmpty(map)){
            Iterator<Map.Entry<K, V>> iterator=map.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<K,V> entityEntry=iterator.next();
                K k=entityEntry.getKey();
                V v=entityEntry.getValue();
                if (v!=null){
                    if (ObjectUtils.isNotEmpty(actions)){
                        for (ITraverseAction<K,V> action:actions
                        ) {
                            action.onTraversed(k,v);
                        }
                    }

                }
            }
        }
    }

}

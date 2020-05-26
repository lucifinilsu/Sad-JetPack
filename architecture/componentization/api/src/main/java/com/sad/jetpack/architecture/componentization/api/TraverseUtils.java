package com.sad.jetpack.architecture.componentization.api;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class TraverseUtils {

    public static interface ITraverseAction<K,V>{
        <VE> VE getVE(K k,V v);
        void action(K k,Object v);
    }
    public static  <K,V,VE> void traverseGroup(LinkedHashMap<K,V> map, ITraverseAction<K,V>... actions){
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
                            VE ve=action.getVE(k,v);
                            action.action(k,ve);
                        }
                    }

                }
            }
        }
    }

}

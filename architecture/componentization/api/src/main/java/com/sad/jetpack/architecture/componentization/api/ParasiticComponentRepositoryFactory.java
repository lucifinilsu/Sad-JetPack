package com.sad.jetpack.architecture.componentization.api;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.Weighers;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ParasiticComponentRepositoryFactory implements InstancesRepositoryFactory {

    private Context context;

    public ParasiticComponentRepositoryFactory(Context context) {
        this.context = context;
        initContainer();
    }

    @Override
    public InstancesRepository from(String url, IConstructor allConstructor, Map<String,IConstructor> constructors, IComponentCallableInitializeListener componentCallableInitializeListener) {
        InternalInstancesRepository componentRepository=new InternalInstancesRepository(url);
        List<IComponentCallable> componentCallableInstances =new LinkedList<>();
        MapTraverseUtils.traverseGroup(DYNAMIC_COMPONENT_STORAGE, new MapTraverseUtils.ITraverseAction<Object,List<IComponentCallable>>() {
            @Override
            public void onTraversed(Object hostObejct,List<IComponentCallable> componentCallables) {
                for (IComponentCallable componentCallable:componentCallables
                     ) {
                    try {
                        String cid=componentCallable.componentId();
                        Uri c_uri=Uri.parse(cid);
                        String c_scheme=c_uri.getScheme();
                        String c_host=c_uri.getAuthority();
                        String c_path=c_uri.getPath();

                        Uri uri=Uri.parse(url);
                        String scheme=uri.getScheme();
                        String host=uri.getAuthority();
                        String path=uri.getPath();

                        if (TextUtils.isEmpty(cid) || (c_scheme.equals(scheme) && c_host.equals(host) && c_path.startsWith(path))){
                            if (componentCallableInitializeListener!=null){
                                componentCallableInitializeListener.onComponentCallableInstanceObtained(componentCallable);
                            }
                            componentCallableInstances.add(componentCallable);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        if (componentCallableInitializeListener!=null){
                            componentCallableInitializeListener.onTraverseCRMException(e);
                        }
                    }
                }

            }
        });
        componentRepository.setComponentInstances(componentCallableInstances);
        return componentRepository;
    }

    protected final static ConcurrentLinkedHashMap<Object,List<IComponentCallable>> DYNAMIC_COMPONENT_STORAGE=new ConcurrentLinkedHashMap.Builder<Object,List<IComponentCallable>>()
                    .maximumWeightedCapacity(999)
                    .weigher(Weighers.singleton())
                    .build();
    @Deprecated
    private static void initContainer(){
        /*if (DYNAMIC_COMPONENT_STORAGE==null){
            DYNAMIC_COMPONENT_STORAGE=new ConcurrentLinkedHashMap.Builder<Object,List<IComponentCallable>>()
                    .maximumWeightedCapacity(99)
                    .weigher(Weighers.singleton())
                    .build();
        }*/
    }

    protected static void registerParasiticComponent(Object host,IComponentCallable componentCallable){
        initContainer();
        List<IComponentCallable> componentCallables=DYNAMIC_COMPONENT_STORAGE.get(host);
        if (componentCallables==null){
            componentCallables=new LinkedList<>();
        }
        componentCallables.add(componentCallable);
        DYNAMIC_COMPONENT_STORAGE.put(host,componentCallables);
    }
    protected static void unregisterParasiticComponent(Object host,String curl){
        initContainer();
        List<IComponentCallable> componentCallables=DYNAMIC_COMPONENT_STORAGE.get(host);
        if (componentCallables!=null && !componentCallables.isEmpty()){
            componentCallables.remove(curl);
            DYNAMIC_COMPONENT_STORAGE.put(host,componentCallables);
        }
    }
    protected static void unregisterParasiticComponent(Object host){
        initContainer();
        DYNAMIC_COMPONENT_STORAGE.remove(host);
    }
}

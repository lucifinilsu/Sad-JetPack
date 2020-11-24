package com.sad.jetpack.architecture.componentization.api2;

import android.content.Context;
import android.net.Uri;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParasiticComponentRepositoryFactory implements IComponentRepositoryFactory{

    private Context context;

    public ParasiticComponentRepositoryFactory(Context context) {
        this.context = context;
    }

    @Override
    public IComponentRepository from(String url, IConstructor allConstructor, Map<String, IConstructor> constructors, IComponentInitializeListener listener) {
        InternalComponentRepository componentRepository=new InternalComponentRepository(url);
        LinkedHashMap<IComponent, String> componentInstances =new LinkedHashMap<>();
        MapTraverseUtils.traverseGroup(DYNAMIC_COMPONENT_STORAGE, new MapTraverseUtils.ITraverseAction<Object,ConcurrentLinkedHashMap<IComponent,String>>() {
            @Override
            public void onTraversed(Object host,ConcurrentLinkedHashMap<IComponent,String> components) {

                MapTraverseUtils.traverseGroup(components, new MapTraverseUtils.ITraverseAction<IComponent, String>() {
                    @Override
                    public void onTraversed(IComponent component, String curl) {
                        try {
                            Uri c_uri=Uri.parse(curl);
                            String c_scheme=c_uri.getScheme();
                            String c_host=c_uri.getAuthority();
                            String c_path=c_uri.getPath();

                            Uri uri=Uri.parse(url);
                            String scheme=uri.getScheme();
                            String host=uri.getAuthority();
                            String path=uri.getPath();

                            if (c_scheme.equals(scheme) && c_host.equals(host) && c_path.startsWith(path)){
                                if (listener!=null){
                                    listener.onComponentInstanceObtained(curl,component);
                                }
                                componentInstances.put(component,curl);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            if (listener!=null){
                                listener.onTraverseCRMException(e);
                            }
                        }
                    }
                });


            }
        });
        componentRepository.setComponentInstances(componentInstances);
        return componentRepository;
    }

    protected final static ConcurrentLinkedHashMap<Object,ConcurrentLinkedHashMap<IComponent,String>> DYNAMIC_COMPONENT_STORAGE=new ConcurrentLinkedHashMap.Builder<Object,ConcurrentLinkedHashMap<IComponent,String>>().build();

    protected static void registerParasiticComponent(Object host,IComponent component,String curl){
        ConcurrentLinkedHashMap<IComponent,String> componentStringConcurrentLinkedHashMap=DYNAMIC_COMPONENT_STORAGE.get(host);
        if (componentStringConcurrentLinkedHashMap==null){
            componentStringConcurrentLinkedHashMap=new ConcurrentLinkedHashMap.Builder<IComponent, String>().build();
        }
        componentStringConcurrentLinkedHashMap.put(component,curl);
        DYNAMIC_COMPONENT_STORAGE.put(host,componentStringConcurrentLinkedHashMap);
    }
    protected static void unregisterParasiticComponent(Object host,String curl){
        ConcurrentLinkedHashMap<IComponent,String> componentStringConcurrentLinkedHashMap=DYNAMIC_COMPONENT_STORAGE.get(host);
        if (componentStringConcurrentLinkedHashMap!=null && !componentStringConcurrentLinkedHashMap.isEmpty()){
            componentStringConcurrentLinkedHashMap.remove(curl);
            DYNAMIC_COMPONENT_STORAGE.put(host,componentStringConcurrentLinkedHashMap);
        }
    }
    protected static void unregisterParasiticComponent(Object host){
        DYNAMIC_COMPONENT_STORAGE.remove(host);
    }
}

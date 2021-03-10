package com.sad.jetpack.architecture.componentization.api;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.Weighers;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ParasiticComponentRepositoryFactory implements InstancesRepositoryFactory
{
    protected ParasiticComponentRepositoryFactory(Parcel in) {
    }

    public static final Creator<ParasiticComponentRepositoryFactory> CREATOR = new Creator<ParasiticComponentRepositoryFactory>() {
        @Override
        public ParasiticComponentRepositoryFactory createFromParcel(Parcel in) {
            return new ParasiticComponentRepositoryFactory(in);
        }

        @Override
        public ParasiticComponentRepositoryFactory[] newArray(int size) {
            return new ParasiticComponentRepositoryFactory[size];
        }
    };

    public static InstancesRepositoryFactory newInstance(){
        return new ParasiticComponentRepositoryFactory();
    }
    private ParasiticComponentRepositoryFactory() {
    }

    @Override
    public InstancesRepository from(Context context,String o_url, IConstructor allConstructor, Map<String,IConstructor> constructors, IComponentCallableInitializeListener componentCallableInitializeListener) {
        Uri u=Uri.parse(o_url);
        u=u.buildUpon().clearQuery().build();
        String url=u.toString();
        String scheme=u.getScheme();
        String host=u.getAuthority();
        String path=u.getPath();
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
        Collections.sort(componentCallableInstances, new Comparator<IComponentCallable>() {
            @Override
            public int compare(IComponentCallable o1, IComponentCallable o2) {
                IComponent c1=o1.component();
                IComponent c2=o2.component();
                if (c1!=null && c2!=null){
                    return c2.priority()-c1.priority();
                }
                return 0;
            }
        });
        componentRepository.setComponentInstances(componentCallableInstances);
        return componentRepository;
    }

    protected final static ConcurrentLinkedHashMap<Object,List<IComponentCallable>> DYNAMIC_COMPONENT_STORAGE=new ConcurrentLinkedHashMap.Builder<Object,List<IComponentCallable>>()
                    .maximumWeightedCapacity(999)
                    .weigher(Weighers.singleton())
                    .build();


    protected static void registerParasiticComponent(Object host,IComponentCallable componentCallable){
        List<IComponentCallable> componentCallables=DYNAMIC_COMPONENT_STORAGE.get(host);
        if (componentCallables==null){
            componentCallables=new LinkedList<>();
        }
        componentCallables.add(componentCallable);
        DYNAMIC_COMPONENT_STORAGE.put(host,componentCallables);
    }
    protected static void unregisterParasiticComponent(Object host,String curl){
        List<IComponentCallable> componentCallables=DYNAMIC_COMPONENT_STORAGE.get(host);
        if (componentCallables!=null && !componentCallables.isEmpty()){
            componentCallables.remove(curl);
            DYNAMIC_COMPONENT_STORAGE.put(host,componentCallables);
        }
    }
    protected static void unregisterParasiticComponent(Object host){
        DYNAMIC_COMPONENT_STORAGE.remove(host);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}

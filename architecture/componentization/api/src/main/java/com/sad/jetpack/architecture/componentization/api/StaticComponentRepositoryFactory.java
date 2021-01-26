package com.sad.jetpack.architecture.componentization.api;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.sad.jetpack.architecture.componentization.annotation.Utils;

import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StaticComponentRepositoryFactory implements InstancesRepositoryFactory,Parcelable {
    private int index=-1;

    protected StaticComponentRepositoryFactory(Parcel in) {
        index = in.readInt();
    }

    public static final Creator<StaticComponentRepositoryFactory> CREATOR = new Creator<StaticComponentRepositoryFactory>() {
        @Override
        public StaticComponentRepositoryFactory createFromParcel(Parcel in) {
            return new StaticComponentRepositoryFactory(in);
        }

        @Override
        public StaticComponentRepositoryFactory[] newArray(int size) {
            return new StaticComponentRepositoryFactory[size];
        }
    };

    public static InstancesRepositoryFactory newInstance(){
        return new StaticComponentRepositoryFactory();
    }
    private StaticComponentRepositoryFactory(){
    }
    @Override
    public InstancesRepository from(Context context,String url, IConstructor allConstructor, Map<String,IConstructor> constructors, IComponentCallableInitializeListener componentInitializeListener) {
        InternalInstancesRepository componentRepository=new InternalInstancesRepository(url);
        LinkedHashMap<Object, String> objectInstances =new LinkedHashMap<>();
        List<IComponentCallable> componentCallableInstances =new LinkedList<>();
        index=-1;
        try {
            if (!ObjectUtils.isEmpty(url)){
                if (url.endsWith("/")){
                    url=url.substring(0,url.length()-1);
                }
                LogcatUtils.internalLog("sad-jetpack",">>>>扫描："+url);
                String crmPath= Utils.crmPaths("crm",url);
                traverse(context,crmPath,componentCallableInstances,objectInstances,allConstructor,constructors,componentInitializeListener);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //LogcatUtils.e("sad-jetpack",">>>> componentCallableInstances is "+componentCallableInstances);
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
        componentRepository.setObjectInstances(objectInstances);
        return componentRepository;
    }

    private void traverse(
            Context context,
            String crmPath,
            List<IComponentCallable> componentCallableInstances,
            LinkedHashMap<Object, String> objectInstances,
            IConstructor allConstructor,
            Map<String,IConstructor> constructors,
            IComponentCallableInitializeListener componentCallableInitializeListener
    ){
        String s=readStringFrom(context,crmPath);
        if (!TextUtils.isEmpty(s)){
            index++;
            try {
                JSONObject jsonObject=new JSONObject(s);
                String className=jsonObject.optString("className");
                String c_url=jsonObject.optString("url");
                ComponentClassInfo componentClassInfo=new ComponentClassInfo();
                componentClassInfo.setClassName(className);
                componentClassInfo.setDescription(jsonObject.optString("description"));
                componentClassInfo.setResPath(jsonObject.optString("resPath"));
                componentClassInfo.setUrl(c_url);
                componentClassInfo.setVersion(jsonObject.optInt("version"));
                if (componentCallableInitializeListener!=null){
                    componentCallableInitializeListener.onComponentClassFound(componentClassInfo);
                }
                if (!TextUtils.isEmpty(className)){
                    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                    Class cls=Class.forName(className,true,getClass().getClassLoader());
                    IConstructor constructor=allConstructor!=null?allConstructor:constructors.get(c_url);
                    Object instance=null;
                    if (constructor!=null){
                        instance=constructor.instance(cls);
                    }
                    else {
                        instance=cls.newInstance();
                    }
                    if (IComponent.class.isAssignableFrom(cls)){
                        IComponent component= (IComponent) instance;
                        LogcatUtils.internalLog("sad-jetpack",">>>>'"+crmPath+"' whose type is IComponent is found,it is "+component);
                        IComponentCallable componentCallable=InternalComponentCallable.newBuilder(component)
                                .componentId(c_url)
                                .build()
                                ;
                        if (componentCallableInitializeListener!=null){
                            componentCallable=componentCallableInitializeListener.onComponentCallableInstanceObtained(componentCallable);
                        }
                        componentCallableInstances.add(componentCallable);
                    }
                    else {
                        if (componentCallableInitializeListener!=null){
                            instance=componentCallableInitializeListener.onObjectInstanceObtained(c_url,instance);
                        }
                        objectInstances.put(instance,c_url);
                    }
                    
                }
                else {
                    throw new Exception("the name of CRM whose path is '"+crmPath+"'is empty!!!");
                }
            }catch (Exception e){
                e.printStackTrace();
                if (componentCallableInitializeListener!=null){
                    componentCallableInitializeListener.onTraverseCRMException(e);
                }
            }

        }
        else {
            try {
                String[] nextPlist=context.getAssets().list(crmPath);
                for (String nextPath:nextPlist
                ) {
                    traverse(context,crmPath+ File.separator+nextPath,componentCallableInstances,objectInstances,allConstructor,constructors,componentCallableInitializeListener);
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private String readStringFrom(Context context,String fn){
        try {
            StringBuffer sb=new StringBuffer();
            InputStream stream=context.getAssets().open(fn);
            int l=stream.available();
            byte[]  buffer = new byte[l];
            stream.read(buffer);
            String result =new String(buffer, "utf-8");
            return result;
        }catch (Exception e){
            //e.printStackTrace();
            LogcatUtils.internalLog("sad-jetpack",">>>>"+fn+" is not file");
            return null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
    }
}

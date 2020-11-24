package com.sad.jetpack.architecture.componentization.api2;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sad.jetpack.architecture.componentization.annotation.Utils;

import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StaticComponentRepositoryFactory implements IComponentRepositoryFactory {
    private Context context;
    private int index=-1;
    public StaticComponentRepositoryFactory(Context context){
        this.context=context;
    }
    @Override
    public IComponentRepository from(String url, IConstructor allConstructor,Map<String, IConstructor> constructors, IComponentInitializeListener componentInitializeListener) {
        InternalComponentRepository componentRepository=new InternalComponentRepository(url);
        LinkedHashMap<Object, String> objectInstances =new LinkedHashMap<>();
        LinkedHashMap<IComponent, String> componentInstances =new LinkedHashMap<>();
        index=-1;
        try {
            if (!ObjectUtils.isEmpty(url)){
                if (url.endsWith("/")){
                    url=url.substring(0,url.length()-1);
                }
                Log.e("sad-jetpack",">>>>扫描："+url);
                List<String> crmPaths= Utils.crmPaths(url);
                for (String ermPath:crmPaths
                ) {
                    try {
                        traverse(ermPath,componentInstances,objectInstances,allConstructor,constructors,componentInitializeListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        componentRepository.setComponentInstances(componentInstances);
        componentRepository.setObjectInstances(objectInstances);
        return componentRepository;
    }

    private void traverse(
            String crmPath,
            LinkedHashMap<IComponent, String> componentInstances,
            LinkedHashMap<Object, String> objectInstances,
            IConstructor allConstructor,
            Map<String, IConstructor> constructors,
            IComponentInitializeListener componentInitializeListener
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
                if (componentInitializeListener!=null){
                    componentInitializeListener.onComponentClassFound(componentClassInfo);
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
                        if (componentInitializeListener!=null){
                            component=componentInitializeListener.onComponentInstanceObtained(c_url,component);
                        }
                        componentInstances.put(component,c_url);
                    }
                    else {
                        if (componentInitializeListener!=null){
                            instance=componentInitializeListener.onObjectInstanceObtained(c_url,instance);
                        }
                        objectInstances.put(instance,c_url);
                    }
                    
                }
                else {
                    throw new Exception("the name of CRM whose path is '"+crmPath+"'is empty!!!");
                }
            }catch (Exception e){
                e.printStackTrace();
                if (componentInitializeListener!=null){
                    componentInitializeListener.onTraverseCRMException(e);
                }
            }

        }
        else {
            try {
                String[] nextPlist=context.getAssets().list(crmPath);
                for (String nextPath:nextPlist
                ) {
                    traverse(crmPath+ File.separator+nextPath,componentInstances,objectInstances,allConstructor,constructors,componentInitializeListener);
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
            Log.e("sad-jetpack",">>>>"+fn+" is not file");
            return null;
        }
    }
}

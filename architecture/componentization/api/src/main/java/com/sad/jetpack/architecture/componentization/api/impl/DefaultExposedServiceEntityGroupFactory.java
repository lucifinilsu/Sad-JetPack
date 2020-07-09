package com.sad.jetpack.architecture.componentization.api.impl;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sad.jetpack.architecture.componentization.annotation.Utils;
import com.sad.jetpack.architecture.componentization.api.ExposedServiceRelationMappingElement;
import com.sad.jetpack.architecture.componentization.api.ExposedServiceRelationMappingEntity;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceEntityGroupFactory;

import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;

public class DefaultExposedServiceEntityGroupFactory implements IExposedServiceEntityGroupFactory {
    private Context context;
    public DefaultExposedServiceEntityGroupFactory(Context context){
        this.context=context;
    }
    @Override
    public LinkedHashMap<String, ExposedServiceRelationMappingEntity> getEntityGroup(String url){
        if (Utils.isURL(url)){
            if (url.endsWith("/")){
                url=url.substring(0,url.length()-1);
            }
            return doGetEntityGroup(url);
        }
        return new LinkedHashMap<>();

    }


    private LinkedHashMap<String, ExposedServiceRelationMappingEntity> doGetEntityGroup(String url) {
        LinkedHashMap<String, ExposedServiceRelationMappingEntity> map=new LinkedHashMap<>();
        if (ObjectUtils.isEmpty(url) || !Utils.isURL(url)){
            return map;
        }
        List<String> ermPaths= Utils.ermPaths(url);
        for (String ermPath:ermPaths
             ) {
            try {
                traverse(map,context,ermPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    private void traverse(LinkedHashMap<String, ExposedServiceRelationMappingEntity> map, Context context, String path) throws Exception{
        String s=readStringFrom(context,path);
        if (!TextUtils.isEmpty(s)){
            try {
                JSONObject jsonObject=new JSONObject(s);
                ExposedServiceRelationMappingEntity entity=new ExposedServiceRelationMappingEntity();
                entity.setPath(path);
                ExposedServiceRelationMappingElement element=new ExposedServiceRelationMappingElement();
                JSONObject jo_e=jsonObject.optJSONObject("element");
                element.setClassName(jo_e.optString("class"));
                element.setDecription(jo_e.optString("description"));
                element.setUrl(jo_e.optString("url"));
                entity.setElement(element);
                map.put(path,entity);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            try {
                String[] nextPlist=context.getAssets().list(path);
                for (String nextPath:nextPlist
                ) {
                    traverse(map,context,path+File.separator+nextPath);
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

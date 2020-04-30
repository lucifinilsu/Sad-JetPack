package com.sad.jetpack.architecture.componentization.api;

import android.net.Uri;
import android.text.TextUtils;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ComponentRepositoryManager {

    protected static final ConcurrentMap<String, Class<?>> COMPONENT_CLASS_MAP = new ConcurrentHashMap<>();
    protected static final String PATH_KEY_SEPARATOR="sca#bas%Scbkabf@_sad_@&";
    protected static final String FILE_RELATIONAL_MAPPING="";

    protected static String keyStr(String url,String name){
        return url+PATH_KEY_SEPARATOR+name;
    }
    protected List<Class<?>> findComponentClassesByPath(String url){
        List<Class<?>> classList=new ArrayList<>();
        if (!TextUtils.isEmpty(url)){

            try {
                Uri uri=Uri.parse(url);
                String path=uri.getPath();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return classList;
    }


    protected List<String> getNames(String url){
        List<String> names=new ArrayList<>();
        return names;
    }

    protected void recordRelationalMapping(String mapFile,String url,String name){
        File file=new File(mapFile);

    }

}

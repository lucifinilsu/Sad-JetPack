package com.sad.jetpack.architecture.componentization.api.internal;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.work.ListenableWorker;

import com.sad.jetpack.architecture.componentization.annotation.Utils;
import com.sad.jetpack.architecture.componentization.api.ExposedServiceRelationMappingElement;
import com.sad.jetpack.architecture.componentization.api.ExposedServiceRelationMappingEntity;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceGroupRepository;
import com.sad.jetpack.architecture.componentization.api.IExposedWorkerService;
import com.sad.jetpack.architecture.componentization.api.MapTraverseUtils;

import java.util.LinkedHashMap;

public class InternalExposedServiceGroupRepository implements IExposedServiceGroupRepository{
    private LinkedHashMap<String, ExposedServiceRelationMappingEntity> entityGroup;
    private Context context;
    private LinkedHashMap<String,Class<ListenableWorker>> workerClassGroup=new LinkedHashMap<>();
    private String orgUrl="";
    public InternalExposedServiceGroupRepository(Context context,String orgUrl,LinkedHashMap<String, ExposedServiceRelationMappingEntity> entityGroup){
        this.entityGroup=entityGroup;
        this.context=context;
        this.orgUrl=orgUrl;
        traverse();
    }

    private void traverse(){
        //Log.e("sad-jetpack",">>>>要遍历的实体组："+entityGroup);
        MapTraverseUtils.traverseGroup(entityGroup,
                new MapTraverseUtils.ITraverseAction<String, ExposedServiceRelationMappingEntity>() {

                    @Override
                    public void onTraversed(String ermPath, ExposedServiceRelationMappingEntity entity) {
                        if (entity!=null){
                            ExposedServiceRelationMappingElement element=entity.getElement();
                            if (element!=null){
                                try {
                                    String cn=element.getClassName();
                                    Class cls=null;
                                    if (!TextUtils.isEmpty(cn)){
                                        Thread.currentThread().setContextClassLoader(element.getClass().getClassLoader());
                                        cls=Class.forName(cn,true,element.getClass().getClassLoader());
                                    }
                                    else {
                                        throw new Exception("the name of the ExposedServiceRelationMappingElement of the exposedService whose ermPath is '"+ermPath+"'is empty!!!");
                                    }
                                    if (IExposedWorkerService.class.isAssignableFrom(cls)){
                                        String p="androidx.work";
                                        String wcn= Utils.creatExposedWorkerClassName(cls.getSimpleName(),ermPath);//"ExposedServiceWorker$$"+cls.getSimpleName()+"$$"+ValidUtils.element.getUrl();
                                        String w=p+"."+wcn;
                                        Log.e("sad-jetpack",">>>>查询工作类："+w);
                                        Class wc=Class.forName(w);
                                        workerClassGroup.put(entity.getElement().getUrl(),wc);
                                    }

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
        );
    }

    @Override
    public LinkedHashMap<String, ExposedServiceRelationMappingEntity> entityGroup() {
        return this.entityGroup;
    }


    /*@Override
    public IExposedServiceInstanceConstructor serviceInstance(String ermPath) throws Exception{
        Class cls=serviceClassList().get(ermPath);
        if (cls!=null){
            return new InternalExposedServiceInstanceConstructor(cls);
        }
        else {
            throw new Exception("serviceClass whose ermPth is '"+ermPath+"' is null !!!");
        }
    }

    @Override
    public IExposedServiceInstanceConstructor serviceInstanceFirst() throws Exception{
        if (serviceClassList().entrySet().iterator().hasNext()){
            Map.Entry<String,Class> entityEntry=serviceClassList().entrySet().iterator().next();
            String ermPath=entityEntry.getKey();
            Class cls=entityEntry.getValue();
            if (cls!=null){
                return new InternalExposedServiceInstanceConstructor(cls);
            }
            else {
                throw new Exception("serviceClass whose ermPth is '"+ermPath+"' is null !!!");
            }
        }
        else {
            throw new Exception("serviceClassList is empty !!!");
        }
    }*/

    @Override
    public LinkedHashMap<String, Class<ListenableWorker>> workerClassGroup() {
       return workerClassGroup;
    }

    @Override
    public String orgUrl() {
        return this.orgUrl;
    }


}

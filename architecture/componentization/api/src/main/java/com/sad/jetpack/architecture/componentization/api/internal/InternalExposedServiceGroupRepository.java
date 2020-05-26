package com.sad.jetpack.architecture.componentization.api.internal;
import android.content.Context;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.WorkRequest;

import com.sad.jetpack.architecture.componentization.api.ExposedServiceRelationMappingElement;
import com.sad.jetpack.architecture.componentization.api.ExposedServiceRelationMappingEntity;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceClassFactory;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceGroupRepository;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceInstanceConstructor;
import com.sad.jetpack.architecture.componentization.api.IPerformer;
import com.sad.jetpack.architecture.componentization.api.InternalPerformer;
import com.sad.jetpack.architecture.componentization.api.TraverseUtils;
import com.sad.jetpack.architecture.componentization.api.impl.DefaultExposedServiceClassFactory;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class InternalExposedServiceGroupRepository implements IExposedServiceGroupRepository{
    private LinkedHashMap<String, ExposedServiceRelationMappingEntity> entityGroup;
    private Context context;
    private LinkedHashMap<String,Class> serviceClassGroup=new LinkedHashMap<>();
    private LinkedHashMap<String,Class<ListenableWorker>> workerClassGroup=new LinkedHashMap<>();
    public InternalExposedServiceGroupRepository(Context context,IExposedServiceClassFactory serviceClassFactory,LinkedHashMap<String, ExposedServiceRelationMappingEntity> entityGroup){
        this.entityGroup=entityGroup;
        this.context=context;
        this.serviceClassFactory=serviceClassFactory;
        Log.e("sad-jetpack",">>>>要遍历的实体组："+entityGroup);
        TraverseUtils.traverseGroup(entityGroup,
                new TraverseUtils.ITraverseAction<String, ExposedServiceRelationMappingEntity>() {
                    @Override
                    public ExposedServiceRelationMappingElement getVE(String ermPath,ExposedServiceRelationMappingEntity exposedServiceRelationMappingEntity) {
                        if (exposedServiceRelationMappingEntity!=null){
                            return exposedServiceRelationMappingEntity.getElement();
                        }
                        return null;
                    }

                    @Override
                    public void action(String ermPath, Object v) {
                        if (v!=null){
                            ExposedServiceRelationMappingElement element= (ExposedServiceRelationMappingElement) v;
                            if (InternalExposedServiceGroupRepository.this.serviceClassFactory==null){
                                InternalExposedServiceGroupRepository.this.serviceClassFactory=new DefaultExposedServiceClassFactory();
                            }
                            Class cls=InternalExposedServiceGroupRepository.this.serviceClassFactory.getServiceClass(element);
                            if (cls!=null){
                                serviceClassGroup.put(ermPath,cls);
                                try {
                                    String p="androidx.work";
                                    String wcn="ExposedServiceWorker$$"+cls.getSimpleName();
                                    String w=p+"."+wcn;
                                    Log.e("sad-jetpack",">>>>查询工作类："+w);
                                    Class wc=Class.forName(w);
                                    workerClassGroup.put(ermPath,wc);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

        );

    }
    private IExposedServiceClassFactory serviceClassFactory;

    @Override
    public LinkedHashMap<String, ExposedServiceRelationMappingEntity> entityGroup() {
        return this.entityGroup;
    }




    @Override
    public LinkedHashMap<String, Class> serviceClassList() {
        return serviceClassGroup;
    }

    @Override
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
    }

    @Override
    public LinkedHashMap<String, Class<ListenableWorker>> workerClassGroup() {
       return workerClassGroup;
    }




    @Override
    public IPerformer commit() {
        return new InternalPerformer(this);
    }

}

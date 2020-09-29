package com.sad.jetpack.architecture.componentization.api;

import android.content.Context;
import android.text.TextUtils;

import com.sad.core.async.ISADTaskProccessListener;
import com.sad.core.async.SADTaskRunnable;
import com.sad.core.async.SADTaskSchedulerClient;
import com.sad.jetpack.architecture.componentization.annotation.Utils;
import com.sad.jetpack.architecture.componentization.api.impl.DefaultExposedServiceEntityGroupFactory;
import com.sad.jetpack.architecture.componentization.api.internal.InternalCluster;
import com.sad.jetpack.architecture.componentization.api.internal.InternalExposedServiceGroupRepository;
import com.sad.jetpack.architecture.componentization.api.internal.InternalExposedServiceInstanceConstructor;

import java.util.LinkedHashMap;

public final class ExposedServiceManager implements IExposedServiceManager,IExposedServiceManagerAsync{
    private Context context;
    private ExposedServiceManager(Context context){this.context=context;}
    private ExposedServiceManager(){this.context=this.context;}
    public static IExposedServiceManager newInstance(){
        return new ExposedServiceManager();
    }

    public static IExposedServiceManager newInstance(Context context){
        return new ExposedServiceManager(context);
    }

    private IExposedServiceEntityGroupFactory entityGroupFactory;
    private IExposedServiceEntityGroupFactory.OnExposedServiceRelationMappingEntityFoundListener onExposedServiceRelationMappingEntityFoundListener;

    @Override
    public ICluster cluster(String url){
        IExposedServiceGroupRepository repository=repository(url);
        return new InternalCluster(this.context,repository);
    }

    @Override
    public ExposedServiceManager asyncScanERM() {
        return this;
    }
    @Override
    public ExposedServiceManager entityGroupFactory(IExposedServiceEntityGroupFactory entityGroupFactory){
        this.entityGroupFactory=entityGroupFactory;
        return this;
    }
    @Override
    public ExposedServiceManager entityFoundListener(IExposedServiceEntityGroupFactory.OnExposedServiceRelationMappingEntityFoundListener onExposedServiceRelationMappingEntityFoundListener){
        this.onExposedServiceRelationMappingEntityFoundListener=onExposedServiceRelationMappingEntityFoundListener;
        return this;
    }

    @Override
    public void repository(String url, OnExposedServiceGroupRepositoryFoundListener repositoryFoundListener) {
        if (entityGroupFactory==null){
            entityGroupFactory=new DefaultExposedServiceEntityGroupFactory(this.context);
        }
        SADTaskSchedulerClient.newInstance().execute(new SADTaskRunnable<IExposedServiceGroupRepository>("GET_IExposedServiceGroupRepository", new ISADTaskProccessListener<IExposedServiceGroupRepository>() {
            @Override
            public void onSuccess(IExposedServiceGroupRepository result) {
                if (repositoryFoundListener!=null){
                    repositoryFoundListener.onExposedServiceGroupRepositoryFoundSuccess(new InternalCluster(ExposedServiceManager.this.context,result));
                }
            }

            @Override
            public void onFail(Throwable throwable) {
                if (repositoryFoundListener!=null){
                    repositoryFoundListener.onExposedServiceGroupRepositoryFoundFailure(throwable);
                }
            }

            @Override
            public void onCancel() {

            }
        }) {
            @Override
            public IExposedServiceGroupRepository doInBackground() throws Exception {
                return repository(url);
                //return new InternalExposedServiceGroupRepository(this.context,url,entityGroupFactory.getEntityGroupByUrl(url,onExposedServiceRelationMappingEntityFoundListener));
            }

        });
    }



    private IExposedServiceGroupRepository repository(String url) {
        if (entityGroupFactory==null){
            entityGroupFactory=new DefaultExposedServiceEntityGroupFactory(this.context);
        }
        return new InternalExposedServiceGroupRepository(this.context,url,entityGroupFactory.getEntityGroupByUrl(url,this.onExposedServiceRelationMappingEntityFoundListener));
    }
    @Override
    public ExposedServiceRelationMappingEntity getFirstEntity(String url) throws Exception{
        if (TextUtils.isEmpty(url)){
            throw new Exception("url is null !!!");
        }
        if (url.indexOf("?")!=-1){
            url= Utils.getURL(url);
        }
        IExposedServiceGroupRepository repository=repository(url);
        LinkedHashMap<String,ExposedServiceRelationMappingEntity> entityLinkedHashMap=repository.entityGroup();
        String[] keys=entityLinkedHashMap.keySet().toArray(new String[]{});
        if (keys.length<1){
            throw new Exception("the Entity whose url is '"+url+"' is not exist,may be it is not registered in ERM ,please check the url or exposedService's annotation!!!");
        }
        String key=keys[1];
        ExposedServiceRelationMappingEntity entity=entityLinkedHashMap.get(key);
        return entity;
    }
    @Override
    public IExposedServiceInstanceConstructor getFirst(String url)throws Exception{
        ExposedServiceRelationMappingEntity entity=getFirstEntity(url);
        String className=entity.getElement().getClassName();
        Class cls=Class.forName(className);
        return new InternalExposedServiceInstanceConstructor(cls);
    }

    /*public IExposedServiceGroupRepository get(String url){
        if (entityGroupFactory==null){
            entityGroupFactory=new DefaultExposedServiceEntityGroupFactory(this.context);
        }
        if (serviceClassFactory==null){
            serviceClassFactory=new DefaultExposedServiceClassFactory();
        }
        LinkedHashMap<String, ExposedServiceRelationMappingEntity> entityGroup=entityGroupFactory.getEntityGroup(url);
        Log.e("sad-jetpack",">>>>开始生成Repository："+url+",准备实体组："+entityGroup);
        IExposedServiceGroupRepository repository=new InternalExposedServiceGroupRepository(this.context,serviceClassFactory,entityGroup);
        return repository;
    }
    public static IExposedServiceInstanceConstructor getFirst(String url) throws Exception{
        return ExposedServiceManager.newInstance()
                .get(url)
                .serviceInstanceFirst();
    }*/
}

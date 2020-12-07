package com.sad.jetpack.architecture.componentization.api;

import android.content.Context;

import com.sad.core.async.OverloadPolicy;
import com.sad.core.async.SADExecutor;
import com.sad.core.async.SchedulePolicy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

final class InternalComponentCluster implements IComponentsCluster {

    private Map<String,IConstructor> constructorMap=new HashMap<>();
    private IConstructor allConstructor;
    private IComponentInitializeListener componentInitializeListener;
    private IComponentRepositoryFactory componentRepositoryFactory;
    private Context context;
    protected InternalComponentCluster(Context context){
        this.context=context;
    }


    @Override
    public IComponentsCluster addConstructorToAll(IConstructor constructor) {
        this.allConstructor=constructor;
        return this;
    }

    @Override
    public IComponentsCluster addConstructor(String curl, IConstructor constructor) {
        constructorMap.put(curl,constructor);
        return this;
    }

    @Override
    public IComponentsCluster addConstructors(Map<String, IConstructor> constructors) {
        this.constructorMap.putAll(constructors);
        return this;
    }

    @Override
    public IComponentsCluster instanceInitializeListener(IComponentInitializeListener listener) {
        this.componentInitializeListener=listener;
        return this;
    }

    @Override
    public IComponentsCluster componentRepositoryFactory(IComponentRepositoryFactory componentRepositoryFactory) {
        this.componentRepositoryFactory=componentRepositoryFactory;
        return this;
    }

    @Override
    public IComponentRepository repository(String url) {
        if (componentRepositoryFactory==null){
            componentRepositoryFactory=new StaticComponentRepositoryFactory(context);
        }
        IComponentRepository componentRepository=componentRepositoryFactory.from(url,this.allConstructor,this.constructorMap,this.componentInitializeListener);
        return componentRepository;
    }

    @Override
    public Future<IComponentRepository> repositoryAsync(String url,IComponentRepositoryObtainedCallback callback) {
        Future<IComponentRepository> future=getDefaultExecutor().submit(new Callable<IComponentRepository>() {
            @Override
            public IComponentRepository call() throws Exception {
                IComponentRepository repository= repository(url);
                if (callback!=null){
                    callback.onComponentRepositoryObtained(repository);
                }
                return repository;
            }
        });
        return future;
    }

    private SADExecutor getDefaultExecutor(){
        int CPU_COUNT = Runtime.getRuntime().availableProcessors();
        int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
        long KEEP_ALIVE = 60L;
        SADExecutor executor = new SADExecutor();
        // set temporary parameter just for test
        // 一下参数设置仅用来测试，具体设置看实际情况。
        // number of concurrent threads at the same time, recommended core size is CPU count
        // 开发者均衡性能和业务场景，自己调整同一时段的最大并发数量
        executor.setCoreSize(CPU_COUNT);

        // adjust maximum number of waiting queue size by yourself or based on phone performance
        // 开发者均衡性能和业务场景，自己调整最大排队线程数量
        executor.setQueueSize(MAXIMUM_POOL_SIZE);

        // 任务数量超出[最大并发数]后，自动进入[等待队列]，等待当前执行任务完成后按策略进入执行状态：后进先执行。
        executor.setSchedulePolicy(SchedulePolicy.LastInFirstRun);

        // 后续添加新任务数量超出[等待队列]大小时，执行过载策略：抛弃队列内最旧任务。
        executor.setOverloadPolicy(OverloadPolicy.DiscardOldTaskInQueue);

        return executor;
    }
}

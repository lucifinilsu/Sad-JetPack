package com.sad.jetpack.architecture.componentization.api2;

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
    private IComponentCallableInitializeListener componentCallableInitializeListener;
    private InstancesRepositoryFactory intancesRepositoryFactory;
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
    public IComponentsCluster instanceInitializeListener(IComponentCallableInitializeListener listener) {
        this.componentCallableInitializeListener =listener;
        return this;
    }

    @Override
    public IComponentsCluster instancesRepositoryFactory(InstancesRepositoryFactory instancesRepositoryFactory){
        this.intancesRepositoryFactory =instancesRepositoryFactory;
        return this;
    }

    @Override
    public InstancesRepository repository(String url) {
        if (intancesRepositoryFactory ==null){
            intancesRepositoryFactory =new StaticComponentRepositoryFactory(context);
        }
        InstancesRepository instancesRepository= intancesRepositoryFactory.from(url,this.allConstructor,this.constructorMap,this.componentCallableInitializeListener);
        return instancesRepository;
    }

    @Override
    public Future<InstancesRepository> repositoryAsync(String url,IComponentRepositoryObtainedCallback callback) {
        Future<InstancesRepository> future=getDefaultExecutor().submit(new Callable<InstancesRepository>() {
            @Override
            public InstancesRepository call() throws Exception {
                InstancesRepository repository= repository(url);
                if (callback!=null){
                    callback.onInstancesRepositoryObtained(repository);
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

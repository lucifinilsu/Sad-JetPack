package com.sad.jetpack.architecture.componentization.api;

import androidx.work.Constraints;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import androidx.work.ExposedServiceWorker;
import androidx.work.WorkRequest;

import com.sad.jetpack.architecture.componentization.api.impl.DefaultWorkRequestFactory;

import java.util.LinkedHashMap;

public class InternalPerformer implements IPerformer {
    private IExposedServiceGroupRepository repository;
    private IWorkerRequestFactory requestFactory;
    public InternalPerformer(IExposedServiceGroupRepository repository){
        this.repository=repository;
    }



    @Override
    public IPerformer requestFactory(IWorkerRequestFactory factory) {
        this.requestFactory=factory;
        return this;
    }

    @Override
    public IPerformer constraints(Constraints constraints) {
        return null;
    }

    @Override
    public void perform(IWorkerDispatcher dispatcher) {
        if (dispatcher!=null){
            LinkedHashMap<String,Class<ListenableWorker>> workerClassGroup=repository.workerClassGroup();
            LinkedHashMap<String, WorkRequest> workRequestGroup=new LinkedHashMap<>();
            TraverseUtils.traverseGroup(workerClassGroup, new TraverseUtils.ITraverseAction<String, Class<ListenableWorker>>() {
                @Override
                public WorkRequest getVE(String ermPath,Class workerClass) {
                    if (workerClass!=null){
                        if (requestFactory==null){
                            requestFactory=new DefaultWorkRequestFactory();
                        }
                        if (constraints==null){
                            constraints=getDefaultConstraints();
                        }
                        return requestFactory.workerRequest(ermPath,workerClass,constraints);
                    }
                    return null;
                }

                @Override
                public void action(String s, Object v) {
                    if (v!=null){
                        workRequestGroup.put(s, (WorkRequest) v);
                    }
                }
            });
            dispatcher.onWorkerDispatched(WorkManager.getInstance(InternalContextHolder.get().getContext()),workRequestGroup);
        }
    }

    private Constraints constraints;
    private Constraints getDefaultConstraints(){
        // 设置限定条件
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)  // 网络状态
                .setRequiresBatteryNotLow(false)                 // 可在电量不足时执行
                .setRequiresCharging(false)                      // 可在不充电时执行
                .setRequiresStorageNotLow(false)                 // 可在存储容量不足时执行
                .setRequiresDeviceIdle(false)                    // 在待机状态下执行
                .build();
        return constraints;
    }
}

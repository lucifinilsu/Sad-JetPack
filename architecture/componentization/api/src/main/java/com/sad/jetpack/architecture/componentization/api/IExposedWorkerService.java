package com.sad.jetpack.architecture.componentization.api;

import androidx.work.ListenableWorker;

import com.sad.jetpack.architecture.componentization.annotation.ExposedService;

public interface IExposedWorkerService extends IExposedService{

    //ListenableWorker.Result actionForWorker(IPCSession<ListenableWorker.Result> session, ListenableWorker worker);

    ListenableWorker.Result actionForWorker(IPCMessenger messenger);

    @Override
    default <T> T action(IPCMessenger messenger){return null;};

    default boolean asyncWork(){
        ExposedService exposedService=info();
        if (exposedService!=null){
            return exposedService.asyncWorker();
        }
        return false;
    };

}

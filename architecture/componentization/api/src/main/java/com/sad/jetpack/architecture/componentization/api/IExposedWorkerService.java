package com.sad.jetpack.architecture.componentization.api;

import androidx.work.ListenableWorker;

import com.sad.jetpack.architecture.componentization.annotation.ExposedService;

import java.lang.annotation.Annotation;

public interface IExposedWorkerService extends IExposedService{

    ListenableWorker.Result actionForWorker(IExposedActionNotifier<ListenableWorker.Result> notifier, ListenableWorker worker);

    @Override
    default <T> T action(IExposedActionNotifier notifier, Object... params) {
        return null;
    }

    default boolean asyncWork(){
        ExposedService exposedService=info();
        if (exposedService!=null){
            return exposedService.asyncWorker();
        }
        return false;
    };

}

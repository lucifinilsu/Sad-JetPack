package com.sad.jetpack.architecture.componentization.api;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ListenableWorker;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.LinkedHashMap;

public interface IPerformer {

    IPerformer requestFactory(IWorkerRequestFactory factory);

    IPerformer constraints(Constraints constraints);

    void performByWorkerRequest(IWorkerDispatcher dispatcher);

    interface IWorkerRequestFactory{

        WorkRequest workerRequest(String ermPath,Class<ListenableWorker> workerClass,Constraints defaultConstraints);

    }

    interface IWorkerDispatcher{

        void onWorkerDispatched(WorkManager manager, LinkedHashMap<String,WorkRequest> requestGroup);

    }

    interface IServiceInstanceDisPatcher{

        void onServiceInstanceDisPatched(LinkedHashMap<String,IExposedServiceInstanceConstructor> instanceConstructors);

    }

}

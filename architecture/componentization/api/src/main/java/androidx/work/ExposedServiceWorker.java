package androidx.work;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.impl.utils.futures.SettableFuture;

import com.google.common.util.concurrent.ListenableFuture;
import com.sad.jetpack.architecture.componentization.api.IExposedActionNotifier;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IExposedWorkerService;

public abstract class ExposedServiceWorker extends ListenableWorker {
    private IExposedWorkerService service;
    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    @Keep
    public ExposedServiceWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        service=exposedWorkerServiceInstance(appContext,workerParams);
    }
    private SettableFuture<Result> mFuture;
    @NonNull
    @Override
    @SuppressLint("RestrictedApi")
    public ListenableFuture<Result> startWork() {
        mFuture = SettableFuture.create();
        try {
            if (service == null) {
                mFuture.setException(new Exception("ExposedWorkerService is null ！！！"));
                return mFuture;
            }
            if (service.asyncWork()) {
                service.actionForWorker(new IExposedActionNotifier<Result>() {
                    @Override
                    public boolean notifyBy(Result o) {
                        mFuture.set(o);
                        return false;
                    }
                }, this);
            } else {
                Result result = service.actionForWorker(null, this);
                mFuture.set(result);
            }
        }catch (Exception e){
            e.printStackTrace();
            mFuture.setException(e);
        }

        return mFuture;

    }


    public abstract IExposedWorkerService exposedWorkerServiceInstance(@NonNull Context appContext, @NonNull WorkerParameters workerParams);


}
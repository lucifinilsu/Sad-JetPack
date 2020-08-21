package androidx.work;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.work.impl.utils.futures.SettableFuture;

import com.google.common.util.concurrent.ListenableFuture;
import com.sad.jetpack.architecture.componentization.api.DataState;
import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.architecture.componentization.api.IPCMessenger;
import com.sad.jetpack.architecture.componentization.api.IExposedWorkerService;
import com.sad.jetpack.architecture.componentization.api.impl.DataCarrierImpl;
import com.sad.jetpack.security.SecurityImpl;

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
                /*service.actionForWorker(new IPCSession<Result>() {
                    @Override
                    public boolean chat(IPCSession session, Result o) {
                        mFuture.set(o);
                        return false;
                    }
                }, this);*/
                service.actionForWorker(new IPCMessenger() {
                    @Override
                    public boolean reply(IDataCarrier d) {
                        mFuture.set(d.data());
                        return false;
                    }

                    @Override
                    public String messengerId() {
                        return getId().toString();
                    }

                    @Override
                    public IDataCarrier extraMessage() {
                        return DataCarrierImpl.newInstanceCreator()
                                .data(ExposedServiceWorker.this)
                                .state(DataState.DONE)
                                .create()
                                ;
                    }
                });

            } else {
                Result result = service.actionForWorker(new IPCMessenger() {
                    @Override
                    public boolean reply(IDataCarrier d) {
                        return false;
                    }

                    @Override
                    public String messengerId() {
                        return null;
                    }

                    @Override
                    public IDataCarrier extraMessage() {
                        return
                                DataCarrierImpl.newInstanceCreator()
                                        .data(ExposedServiceWorker.this)
                                        .state(DataState.DONE)
                                        .create()
                                ;
                    }
                });
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

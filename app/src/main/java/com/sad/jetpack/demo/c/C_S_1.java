package com.sad.jetpack.demo.c;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.sad.jetpack.architecture.componentization.annotation.ExposedService;
import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IPCMessenger;
import com.sad.jetpack.architecture.componentization.api.IPCSession;
import com.sad.jetpack.architecture.componentization.api.impl.DataCarrierImpl;

@ExposedService(url = "test://group/c/s/1")
public class C_S_1 implements IExposedService, LifecycleOwner,IPCSession{
    @Override
    public <T> T action(IPCMessenger messenger) {
        Log.e("sad-jetpack","------------->并行同步任务1");
        IDataCarrier dataCarrier=messenger.extraMessage();
        if (dataCarrier==null){
            dataCarrier= DataCarrierImpl.newInstanceCreator().data("c_c_1").create();
        }
        dataCarrier.creator().data("c_c_1").create();
        messenger.reply(dataCarrier, new IPCSession() {
            @Override
            public boolean componentChat(IDataCarrier o, IPCMessenger messenger) {
                return false;
            }
        });
        return null;
    }

    @Override
    public int priority() {
        return 99;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return new Lifecycle() {
            @Override
            public void addObserver(@NonNull LifecycleObserver observer) {

            }

            @Override
            public void removeObserver(@NonNull LifecycleObserver observer) {

            }

            @NonNull
            @Override
            public State getCurrentState() {
                return null;
            }
        };
    }

    @Override
    public boolean componentChat(IDataCarrier o, IPCMessenger messenger) {

        return false;
    }
}

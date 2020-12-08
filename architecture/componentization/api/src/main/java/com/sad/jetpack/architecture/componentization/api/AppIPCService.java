package com.sad.jetpack.architecture.componentization.api;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;

import androidx.annotation.Nullable;



public class AppIPCService extends Service {
    private Handler serverHandler=new AppMessengerMasterHandler();
    private Messenger serverMessenger;
    @Override
    public void onCreate() {
        super.onCreate();
        if (serverMessenger==null){
            serverMessenger=new Messenger(serverHandler);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serverMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.e("ipc","---------------->服务端被调用");
        return START_NOT_STICKY;
    }
}

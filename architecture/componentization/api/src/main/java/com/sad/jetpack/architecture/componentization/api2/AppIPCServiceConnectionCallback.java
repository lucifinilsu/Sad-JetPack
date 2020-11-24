package com.sad.jetpack.architecture.componentization.api2;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;

public interface AppIPCServiceConnectionCallback extends ServiceConnection {

    void onGetMessenger(ComponentName name, Messenger serverMessenger);

    @Override
    default void onServiceConnected(ComponentName name, IBinder binder) {
        //做该做的事情,将远程的binder包装成远程信使供本地存储
        Messenger serverMessenger = new Messenger(binder);
        onGetMessenger(name,serverMessenger);
    }

    @Override
    default void onServiceDisconnected(ComponentName name) {}
}

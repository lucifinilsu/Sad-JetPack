package com.sad.jetpack.architecture.componentization.api2;

import android.os.Message;

import java.util.concurrent.Future;

public interface IComponentCallable {

    IComponent component();

    <T> T call(Message message);

    <T> T call(Message message,IPCResultCallback callback);

    <T> T call(Message message,long timeout,IPCResultCallback callback);

    void call(Message message,long timeout,long delay,IPCResultCallback callback);
}
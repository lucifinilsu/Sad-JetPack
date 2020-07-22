package com.sad.jetpack.architecture.componentization.api;
@Deprecated
public interface IFuture<T> {

    T get();

    IPCSession openSession(IPCMessenger messenger);
}

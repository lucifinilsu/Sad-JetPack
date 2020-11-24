package com.sad.jetpack.architecture.componentization.api2;

public interface IFuture<T> {

    T get();

    void cancel();
}

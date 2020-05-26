package com.sad.jetpack.architecture.componentization.api;

public interface IExposedActionNotifier<D> {

    boolean notifyBy(D d);

}

package com.sad.jetpack.architecture.componentization.api;

public interface IPCExceptionListener {

    void onException(IPCMessageTransmissionConfig transmissionConfig,Throwable throwable);
}

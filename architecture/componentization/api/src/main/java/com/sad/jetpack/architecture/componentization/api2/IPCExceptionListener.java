package com.sad.jetpack.architecture.componentization.api2;

public interface IPCExceptionListener {

    void onException(IPCMessageTransmissionConfig transmissionConfig,Throwable throwable);
}

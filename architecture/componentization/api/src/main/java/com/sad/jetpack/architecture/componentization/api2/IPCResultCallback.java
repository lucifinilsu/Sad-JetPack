package com.sad.jetpack.architecture.componentization.api2;

import android.os.Message;

public interface IPCResultCallback extends IPCExceptionListener{

    void onDone(Message msg, IPCMessageTransmissionConfig transmissionConfig);

}
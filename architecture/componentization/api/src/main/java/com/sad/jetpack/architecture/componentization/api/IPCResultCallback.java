package com.sad.jetpack.architecture.componentization.api;

import android.os.Message;

public interface IPCResultCallback extends IPCExceptionListener{

    void onDone(Message msg, IPCMessageTransmissionConfig transmissionConfig);

}

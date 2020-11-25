package com.sad.jetpack.architecture.componentization.api2;

import android.os.Message;
import android.os.Messenger;

public interface IPCMessageSender {

    boolean sendMessage(Messenger messenger,Message message,IPCResultCallback callback) throws Exception;

}
package com.sad.jetpack.architecture.componentization.api.remote;

import android.os.Bundle;
import android.os.Messenger;

import com.sad.jetpack.architecture.componentization.api.IProceedListener;

public interface IPCTransmission {

    int REGISTER_TO_MESSENGERS_POOL=1;
    int UNREGISTER_FROM_MESSENGERS_POOL=2;
    int CREATE_REMOTE_IPC_CHAT =4;

    IPCTransmission classLoader(ClassLoader classLoader);

    IPCTransmission toApp(String app);

    IPCTransmission toProcess(String process);

    IPCTransmission messenger(Messenger messenger);

    IPCTransmission replyTo(Messenger replyToMessenger);

    IPCTransmission target(int target);

    IPCTransmission bundle(Bundle bundle);

    IPCTransmission listener(IProceedListener listener);

    void submit();

}

package com.sad.jetpack.architecture.componentization.api2;

import android.os.Messenger;

public interface IPCMessenger {

    Messenger messenger();

    String id();

}

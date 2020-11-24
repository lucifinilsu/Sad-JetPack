package com.sad.jetpack.architecture.componentization.api2;

import android.os.Parcelable;

public interface IPCMessageTransmissionConfig extends Parcelable {
    String fromApp();
    String fromProcess();
    IPCTarget target();
    int destType();
}

package com.sad.jetpack.architecture.componentization.api2;

import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;

public interface IPCTarget extends Parcelable {

    int PROCESSOR_MODE_SEQUENCE=0;

    int PROCESSOR_MODE_CONCURRENCY=1;

    int SINGLE=-1;

    String toApp();

    String toProcess();

    String url();

    int prcessorMode();

    long delay();

    long timeout();

}

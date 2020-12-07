package com.sad.jetpack.architecture.componentization.api2;

import android.os.Parcelable;

public interface ICallerConfig extends Parcelable {

    long delay();

    long timeout();

    Builder toBuilder();

    interface Builder{

        Builder delay(long delay);

        Builder timeout(long timeout);

        ICallerConfig build();
    }
}

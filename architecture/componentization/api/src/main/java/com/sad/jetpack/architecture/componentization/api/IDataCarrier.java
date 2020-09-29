package com.sad.jetpack.architecture.componentization.api;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import com.sad.jetpack.security.ISecurity;
public interface IDataCarrier extends Parcelable {

    <T> T data();

    DataState state();

    Creator creator();

    interface Creator{

        Creator data(Object o);

        Creator state(DataState state);

        IDataCarrier create();
    }

}

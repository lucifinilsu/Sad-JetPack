package com.sad.jetpack.architecture.componentization.api;
import androidx.annotation.NonNull;
import com.sad.jetpack.security.ISecurity;
public interface IDataCarrier {

    <T> T data();

    DataState state();

    Creator creator();

    interface Creator{

        Creator data(Object o);

        Creator state(DataState state);

        IDataCarrier create();
    }

}

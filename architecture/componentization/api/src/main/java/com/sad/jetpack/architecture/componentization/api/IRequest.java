package com.sad.jetpack.architecture.componentization.api;

import android.os.Parcelable;

import java.util.Map;

public interface IRequest extends Parcelable{

    String fromApp();

    String fromProcess();

    IDataContainer dataContainer();

    String id();

    Builder toBuilder();

    interface Builder{

        Builder fromApp(String fromApp);

        Builder fromProcess(String fromProcess);

        Builder dataContainer(IDataContainer dataContainer);

        Builder addData(String name,Object data);

        Builder addData(Map data);

        Builder addData(IDataContainer data);

        IRequest build();
    }

}

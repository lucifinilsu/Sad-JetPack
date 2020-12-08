package com.sad.jetpack.architecture.componentization.api;

import android.os.Parcelable;

public interface IResponse extends Parcelable {

    IRequest request();

    IDataContainer dataContainer();

    Builder toBuilder();

    interface Builder{

        Builder request(IRequest request);

        Builder dataContainer(IDataContainer dataContainer);

        IResponse build();

    }

}

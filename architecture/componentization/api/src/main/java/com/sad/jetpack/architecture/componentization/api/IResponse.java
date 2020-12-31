package com.sad.jetpack.architecture.componentization.api;

import android.os.Parcel;
import android.os.Parcelable;

public interface IResponse extends IParcelable<IResponse> {

    IRequest request();

    IBody body();

    Builder toBuilder();

    @Override
    default IResponse readFromParcel(Parcel in){
        IResponse response=toBuilder()
                .request(in.readParcelable(IRequest.class.getClassLoader()))
                .body(in.readParcelable(IBody.class.getClassLoader()))
                .build();
        return response;
    }

    @Override
    default void writeToParcel(Parcel dest, int flags){
        dest.writeParcelable(request(),flags);
        dest.writeParcelable(body(),flags);
    }

    interface Builder{

        Builder request(IRequest request);

        Builder body(IBody body);

        IResponse build();

    }

}

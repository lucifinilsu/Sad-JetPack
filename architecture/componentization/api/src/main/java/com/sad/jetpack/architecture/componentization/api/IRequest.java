package com.sad.jetpack.architecture.componentization.api;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public interface IRequest extends IParcelable<IRequest>{

    String fromApp();

    String fromProcess();

    IBody body();

    String id();

    IResponse previousResponse();

    /*boolean parseUrlParameters();*/

    Builder toBuilder();

    @Override
    default IRequest readFromParcel(Parcel in){
        IRequest request=toBuilder()
                .fromApp(in.readString())
                .fromProcess(in.readString())
                .body(in.readParcelable(IBody.class.getClassLoader()))
                .id(in.readString())
                /*.parseUrlParameters(in.readInt()==0)*/
                .previousResponse(in.readParcelable(IResponse.class.getClassLoader()))
                .build();
        return request;
    }

    @Override
    default void writeToParcel(Parcel dest, int flags){
        dest.writeString(fromApp());
        dest.writeString(fromProcess());
        dest.writeParcelable(body(),flags);
        dest.writeString(id());
        /*dest.writeInt(parseUrlParameters()?0:1);*/
        dest.writeParcelable(previousResponse(),flags);

    }

    interface Builder{

        Builder fromApp(String fromApp);

        Builder fromProcess(String fromProcess);

        Builder body(IBody body);

        Builder id(String id);

        Builder previousResponse(IResponse response);

        /*Builder parseUrlParameters(boolean p);*/

        IRequest build();
    }

}

package com.sad.jetpack.architecture.componentization.api;

import android.os.Parcel;

import java.util.Map;

public interface IBody extends IParcelable<IBody>{

    IDataContainer dataContainer();

    <T> T extraObject();

    int what();

    String description();

    Builder toBuilder();

    @Override
    default IBody readFromParcel(Parcel in){
        IBody body=toBuilder()
                .dataContainer((IDataContainer) in.readSerializable())
                .what(in.readInt())
                .description(in.readString())
                .extraObject(in.readValue(getClass().getClassLoader()))
                .build();
        return body;
    }

    @Override
    default void writeToParcel(Parcel dest, int flags){
        dest.writeSerializable(dataContainer());
        dest.writeInt(what());
        dest.writeString(description());
        dest.writeValue(extraObject());
    }

    interface Builder{

        Builder dataContainer(IDataContainer dataContainer);

        Builder what(int what);

        Builder extraObject(Object extra);

        Builder description(String des);

        Builder addData(String name, Object data);

        Builder addData(Map data);

        Builder addData(IDataContainer data);

        IBody build();
    }
}

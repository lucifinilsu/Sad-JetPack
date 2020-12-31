package com.sad.jetpack.architecture.componentization.api.extension.router;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

public interface IRouterParameters<I extends IRouterParameters<I,B>,B extends IRouterParameters.Builder<B,I>> extends IRouterParametersParcelable<I,B>{

    String action();

    List<Integer> flags();

    Bundle bundle();

    String targetPackage();

    Uri uri();

    B toBuilder();

    interface Builder<B extends Builder<B,I>,I extends IRouterParameters<I,B>>{

        B action(String action);

        B addFlags(Integer... flags);

        B addFlags(ArrayList<Integer> flags);

        B bundle(Bundle bundle);

        B targetPackage(String pkg);

        B uri(Uri uri);

        I build();
    }


    @Override
    default  I readFromParcel(Parcel in){
        B builder=toBuilder();
        builder.action(in.readString());
        builder.addFlags(in.readArrayList(getClass().getClassLoader()));
        builder.bundle(in.readBundle());
        builder.targetPackage(in.readString());
        builder.uri(in.readParcelable(Uri.class.getClassLoader()));
        return builder.build();
    }

    @Override
    default void writeToParcel(Parcel dest, int flags){
        dest.writeString(action());
        dest.writeList(flags());
        dest.writeBundle(bundle());
        dest.writeString(targetPackage());
        dest.writeParcelable(uri(),flags);
    }
}

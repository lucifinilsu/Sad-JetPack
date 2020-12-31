package com.sad.jetpack.architecture.componentization.api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2019/5/10 0010.
 */

public interface IParcelable<T> extends Parcelable{

    @Override
    default int describeContents(){return 0;}

    T readFromParcel(Parcel in);

}

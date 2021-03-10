package com.sad.jetpack.architecture.componentization.api.extension.router;

import android.os.Bundle;
import android.os.Parcel;

import androidx.activity.result.ActivityResultLauncher;


public interface IActivityRouterParameters<I extends IActivityRouterParameters<I,B>,B extends IActivityRouterParameters.Builder<B,I>> extends IRouterParameters<I,B>{

    int requestCode();

    Bundle options();

    ActivityResultLauncher resultLauncher();

    B toBuilder();

    interface Builder<B extends Builder<B,I>,I extends IActivityRouterParameters<I,B>> extends IRouterParameters.Builder<B,I>{

        B requestCode(int code);

        B options(Bundle options);

        B resultLauncher(ActivityResultLauncher resultLauncher);

        I build();
    }

    @Override
    default I readFromParcel(Parcel in) {
        I base=IRouterParameters.super.readFromParcel(in);
        B builder=base.toBuilder();
        I curr=builder
                .requestCode(in.readInt())
                .options(in.readBundle())
                .resultLauncher((ActivityResultLauncher) in.readValue(getClass().getClassLoader()))
                .build()
                ;
        return curr;
    }

    @Override
    default void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(requestCode());
        dest.writeBundle(options());
        dest.writeValue(resultLauncher());
    }
}

package com.sad.jetpack.architecture.componentization.api.extension.router;

import android.os.Bundle;
import android.os.Parcel;

import androidx.activity.result.ActivityResultLauncher;

import com.sad.jetpack.architecture.componentization.api.IRequest;

public interface IActivityRouterParameters<I extends IActivityRouterParameters<I,B>,B extends IActivityRouterParameters.Builder<B,I>> extends IRouterParameters<I,B>{

    Bundle transitionBundle();

    int requestCode();

    ActivityResultLauncher resultLauncher();

    B toBuilder();

    interface Builder<B extends Builder<B,I>,I extends IActivityRouterParameters<I,B>> extends IRouterParameters.Builder<B,I>{

        B transitionBundle(Bundle transitionBundle);

        B requestCode(int code);

        B resultLauncher(ActivityResultLauncher resultLauncher);

        I build();
    }

    @Override
    default I readFromParcel(Parcel in) {
        I base=IRouterParameters.super.readFromParcel(in);
        B builder=base.toBuilder();
        I curr=builder
                .transitionBundle(in.readBundle())
                .requestCode(in.readInt())
                .resultLauncher((ActivityResultLauncher) in.readValue(getClass().getClassLoader()))
                .build()
                ;
        return curr;
    }

    @Override
    default void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(bundle());
        dest.writeInt(requestCode());
        dest.writeValue(resultLauncher());
    }
}

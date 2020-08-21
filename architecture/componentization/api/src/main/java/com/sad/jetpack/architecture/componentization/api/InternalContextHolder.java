package com.sad.jetpack.architecture.componentization.api;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import java.util.LinkedHashMap;

final class InternalContextHolder {

    @SuppressLint("StaticFieldLeak")
    private static volatile InternalContextHolder instance;
    private Context mContext;
    private InternalContextHolder(Context context) {
        mContext = context;
    }

    /**
     * 获取实例
     */
    public static InternalContextHolder get() {
        if (instance == null) {
            synchronized (InternalContextHolder.class) {
                if (instance == null) {
                    Context context = InternalContextInitializerProvider.mContext;
                    if (context == null) {
                        throw new IllegalStateException("context == null");
                    }
                    instance = new InternalContextHolder(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取上下文
     */
    public Context getContext() {
        return mContext;
    }
}


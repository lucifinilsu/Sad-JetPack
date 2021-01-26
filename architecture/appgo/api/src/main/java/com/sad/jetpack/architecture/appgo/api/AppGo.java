package com.sad.jetpack.architecture.appgo.api;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class AppGo {

    @SuppressLint("StaticFieldLeak")
    private static volatile AppGo instance;
    private Context mContext;

    private AppGo(Context context) {
        mContext = context;
    }

    public static AppGo init(Context context){
        ApplicationContextInitializerProvider.mContext=context;
        return get();
    }

    /**
     * 获取实例
     */
    public static AppGo get() {
        if (instance == null) {
            synchronized (AppGo.class) {
                if (instance == null) {
                    Context context = ApplicationContextInitializerProvider.mContext;
                    if (context == null) {
                        //throw new IllegalStateException("context == null");
                    }
                    instance = new AppGo(context);
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

    public Application getApplication() {
        return (Application) mContext.getApplicationContext();
    }
}


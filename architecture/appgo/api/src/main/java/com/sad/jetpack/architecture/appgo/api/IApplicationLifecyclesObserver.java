package com.sad.jetpack.architecture.appgo.api;


import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public interface IApplicationLifecyclesObserver {

    default public void onApplicationCreate(Application application){};

    default public void onApplicationLowMemory(){};

    default public void onApplicationConfigurationChanged(Configuration newConfig){};

    default public void onApplicationTerminate(){};

    default public void onApplicationTrimMemory(int level){};

    default public void attachApplicationBaseContext(Context base){};


}

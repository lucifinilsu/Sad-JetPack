package com.sad.jetpack.architecture.appgo.api;


import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public interface IApplicationLifecyclesObserver {

    default public void onApplicationCreated(Application application){};

    default public void onApplicationLowMemory(){};

    default public void onApplicationConfigurationChanged(Configuration newConfig){};

    default public void onApplicationTerminate(){};

    default public void onApplicationTrimMemory(int level){};

    default public void attachApplicationBaseContext(Context base){};


    //Pre

    default public void onApplicationPreCreated(Application application){};

    default public void onApplicationPreLowMemory(){};

    default public void onApplicationPreConfigurationChanged(Configuration newConfig){};

    default public void onApplicationPreTerminate(){};

    default public void onApplicationPreTrimMemory(int level){};

    default public void attachApplicationPreBaseContext(Context base){};

}

package com.sad.jetpack.demo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.sad.jetpack.architecture.appgo.api.AppGo;
import com.sad.jetpack.architecture.componentization.api.LogcatUtils;


import com.sad.jetpack.architecture.appgo.annotation.ApplicationAccess;
import com.sad.jetpack.architecture.componentization.api.SCore;


@ApplicationAccess
public class App extends Application {
    /*@Override
    public void onCreate() {
        super.onCreate();
        LogcatUtils.e("test","------->i am onreate");
        //AppGo.init(this);
    }*/


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}

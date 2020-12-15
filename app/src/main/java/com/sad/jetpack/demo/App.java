package com.sad.jetpack.demo;

import android.app.Application;
import android.content.Context;
import com.sad.jetpack.architecture.componentization.api.LogcatUtils;


import com.sad.jetpack.architecture.appgo.annotation.ApplicationAccess;


@ApplicationAccess
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogcatUtils.e("test","------->i am onreate");
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}

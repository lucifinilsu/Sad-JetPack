package com.sad.jetpack.demo;

import android.app.Application;

import com.sad.jetpack.architecture.appgo.annotation.ApplicationAccess;
@ApplicationAccess
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }
}

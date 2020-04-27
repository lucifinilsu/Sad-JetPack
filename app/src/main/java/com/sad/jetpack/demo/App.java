package com.sad.jetpack.demo;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;

import com.sad.jetpack.architecture.appgo.annotation.ApplicationAccess;
@ApplicationAccess
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("test","------->i am onreate");
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}

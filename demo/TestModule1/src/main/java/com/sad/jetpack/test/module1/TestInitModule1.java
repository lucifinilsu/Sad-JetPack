package com.sad.jetpack.test.module1;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.sad.jetpack.architecture.appgo.annotation.ApplicationLifeCycleAction;
import com.sad.jetpack.architecture.appgo.api.IApplicationLifecyclesObserver;

public class TestInitModule1 implements IApplicationLifecyclesObserver {
    @ApplicationLifeCycleAction(priority = 1561)
    @Override
    public void onApplicationConfigurationChanged(Application application, Configuration newConfig) {

    }
    @ApplicationLifeCycleAction(priority = 1561)
    @Override
    public void attachApplicationBaseContext(Application application,Context base) {

    }
}

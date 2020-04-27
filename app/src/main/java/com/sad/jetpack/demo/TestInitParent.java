package com.sad.jetpack.demo;

import android.app.Application;
import android.content.res.Configuration;

import com.sad.jetpack.architecture.appgo.annotation.ApplicationLifeCycleAction;
import com.sad.jetpack.architecture.appgo.api.IApplicationLifecyclesObserver;

public class TestInitParent implements IApplicationLifecyclesObserver {
    @ApplicationLifeCycleAction(priority = 165,processName = {"xxx","14csaasc6","csacavw"})
    @Override
    public void onApplicationCreated(Application application) {
        //do sth
    }
    @ApplicationLifeCycleAction(priority = 1000,processName = "1321ss")
    @Override
    public void onApplicationConfigurationChanged(Application application, Configuration newConfig) {
        //do sth
    }
}

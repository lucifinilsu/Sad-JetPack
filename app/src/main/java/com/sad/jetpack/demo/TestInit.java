package com.sad.jetpack.demo;

import android.app.Application;

import com.sad.jetpack.architecture.appgo.annotation.ApplicationLifeCycleAction;
import com.sad.jetpack.architecture.appgo.api.IApplicationLifecyclesObserver;


public class TestInit extends TestInitParent implements IApplicationLifecyclesObserver {

    @ApplicationLifeCycleAction(processName = {"123456"},priority = 199)
    @Override
    public void onApplicationCreated(Application application) {
        //do sth
    }
    @ApplicationLifeCycleAction(priority = 12,processName = "89r")
    @Override
    public void onApplicationPreCreated(Application application) {

    }
    /*@ApplicationLifeCycleAction(priority = 1156)
    @Override
    public void onApplicationPreConfigurationChanged(Application application, Configuration newConfig) {

    }*/
    @ApplicationLifeCycleAction(priority = 11)
    @Override
    public void onApplicationTrimMemory(Application application, int level) {

    }
}

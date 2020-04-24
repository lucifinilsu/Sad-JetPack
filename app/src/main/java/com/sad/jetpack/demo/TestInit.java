package com.sad.jetpack.demo;

import android.app.Application;

import com.sad.jetpack.architecture.appgo.annotation.ApplicationLifeCycleAction;
import com.sad.jetpack.architecture.appgo.api.IApplicationLifecyclesObserver;

public class TestInit implements IApplicationLifecyclesObserver {
    @ApplicationLifeCycleAction(priority = 165,processName = {"xxx","14csaasc6","csacavw"})
    @Override
    public void onApplicationCreated(Application application) {
        //do sth
    }
}

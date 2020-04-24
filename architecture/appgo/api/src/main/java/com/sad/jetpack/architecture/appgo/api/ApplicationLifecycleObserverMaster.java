package com.sad.jetpack.architecture.appgo.api;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.sad.jetpack.architecture.appgo.annotation.ApplicationLifeCycleAction;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
public class ApplicationLifecycleObserverMaster {

    public static String getCurrAppProccessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {

                return appProcess.processName;
            }
        }
        return "";
    }

    public static void doOnPreCreatedAnchor(Application application, IApplicationLifecyclesObserver lifecyclesObserver, String[] processNames){
        String currProcess=getCurrAppProccessName(application);
        if (processNames==null || processNames.length==0 || java.util.Arrays.asList(processNames).contains(currProcess)){
            lifecyclesObserver.onApplicationPreCreated(application);
        }
    }
    public static void doOnCreatedAnchor(Application application, IApplicationLifecyclesObserver lifecyclesObserver, String[] processNames){
        String currProcess=getCurrAppProccessName(application);
        if (processNames==null || processNames.length==0 || java.util.Arrays.asList(processNames).contains(currProcess)){
            lifecyclesObserver.onApplicationCreated(application);
        }
    }

}

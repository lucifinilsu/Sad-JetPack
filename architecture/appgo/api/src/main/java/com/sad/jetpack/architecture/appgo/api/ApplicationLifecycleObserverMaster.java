package com.sad.jetpack.architecture.appgo.api;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

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

    /*public static void doOnCreatedAnchor(IApplicationLifecyclesObserver lifecyclesObserver, String[] processNames,boolean after,Application application){
        String currProcess=getCurrAppProccessName(application);
        if (processNames==null || processNames.length==0 || java.util.Arrays.asList(processNames).contains(currProcess)){
            if (after){
                lifecyclesObserver.onApplicationCreated(application);
            }
            else {
                lifecyclesObserver.onApplicationPreCreated(application);
            }

        }
    }

    public static void doOnConfigurationChangedAnchor(IApplicationLifecyclesObserver lifecyclesObserver, String[] processNames, boolean after, Application application, Configuration newConfig){
        String currProcess=getCurrAppProccessName(application);
        if (processNames==null || processNames.length==0 || java.util.Arrays.asList(processNames).contains(currProcess)){
            if (after){
                lifecyclesObserver.onApplicationConfigurationChanged(application,newConfig);
            }
            else {
                lifecyclesObserver.onApplicationPreConfigurationChanged(application,newConfig);
            }

        }
    }

    public static void doOnLowMemoryAnchor(IApplicationLifecyclesObserver lifecyclesObserver, String[] processNames,boolean after,Application application){
        String currProcess=getCurrAppProccessName(application);
        if (processNames==null || processNames.length==0 || java.util.Arrays.asList(processNames).contains(currProcess)){
            if (after){
                lifecyclesObserver.onApplicationLowMemory(application);
            }
            else {
                lifecyclesObserver.onApplicationPreLowMemory(application);
            }

        }
    }

    public static void doOnTerminateAnchor(IApplicationLifecyclesObserver lifecyclesObserver, String[] processNames,boolean after,Application application){
        String currProcess=getCurrAppProccessName(application);
        if (processNames==null || processNames.length==0 || java.util.Arrays.asList(processNames).contains(currProcess)){
            if (after){
                lifecyclesObserver.onApplicationTerminate(application);
            }
            else {
                lifecyclesObserver.onApplicationPreTerminate(application);
            }

        }
    }
    public static void doOnTrimMemoryAnchor(IApplicationLifecyclesObserver lifecyclesObserver, String[] processNames,boolean after,Application application,int level){
        String currProcess=getCurrAppProccessName(application);
        if (processNames==null || processNames.length==0 || java.util.Arrays.asList(processNames).contains(currProcess)){
            if (after){
                lifecyclesObserver.onApplicationTrimMemory(application,level);
            }
            else {
                lifecyclesObserver.onApplicationPreTrimMemory(application,level);
            }

        }
    }
    public static void doAttachApplicationBaseContextAnchor(IApplicationLifecyclesObserver lifecyclesObserver, String[] processNames,boolean after,Context context){
        String currProcess=getCurrAppProccessName(context);
        if (processNames==null || processNames.length==0 || java.util.Arrays.asList(processNames).contains(currProcess)){
            if (after){
                lifecyclesObserver.attachApplicationBaseContext(context);
            }
            else {
                lifecyclesObserver.attachApplicationPreBaseContext(context);
            }

        }
    }*/
}

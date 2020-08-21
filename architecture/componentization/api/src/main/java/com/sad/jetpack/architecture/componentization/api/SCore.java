package com.sad.jetpack.architecture.componentization.api;

import android.content.Context;

import com.sad.jetpack.architecture.componentization.api.internal.InternalExposedServiceInstanceConstructor;

import java.util.LinkedHashMap;

public class SCore {

    public static IExposedServiceManager getManager(){
        IExposedServiceManager exposedServiceManager=ExposedServiceManager.newInstance();
        return exposedServiceManager;
    }

}

package com.sad.jetpack.architecture.componentization.api;

import com.sad.jetpack.architecture.componentization.annotation.ExposedService;

import java.lang.annotation.Annotation;

public interface IExposedService {

    default ExposedService info(){
        Annotation[] annotations=getClass().getDeclaredAnnotations();
        ExposedService exposedService = null;
        if (annotations!=null && annotations.length>0){
            for (Annotation a :annotations) {
                if (a instanceof ExposedService){
                    exposedService= (ExposedService) a;
                    break;
                }
            }
            return exposedService;
        }
        else {
            return null;
        }
    }

}

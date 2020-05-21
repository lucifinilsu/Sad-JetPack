package com.sad.jetpack.architecture.componentization.api;

import com.sad.jetpack.architecture.componentization.annotation.ExposedService;

import java.lang.annotation.Annotation;

public interface IExposedService {

    <T> T action(IExposedActionNotifier notifier, Object... params);

    default String url(){
        ExposedService exposedService=info();
        if (exposedService!=null){
            return exposedService.url();
        }
        return "";
    };

    default String description(){
        ExposedService exposedService=info();
        if (exposedService!=null){
            return exposedService.description();
        }
        return "";
    }

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

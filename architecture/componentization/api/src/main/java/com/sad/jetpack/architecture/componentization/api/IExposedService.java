package com.sad.jetpack.architecture.componentization.api;

import com.sad.jetpack.architecture.componentization.annotation.ExposedService;

import java.lang.annotation.Annotation;

public interface IExposedService extends Comparable<IExposedService>{

    <T> T action(IPCMessenger messenger);

    default <T> T action(){
        return action(null);
    };

    default int priority(){return 0;};

    default String[] url(){
        ExposedService exposedService=info();
        if (exposedService!=null){
            return exposedService.url();
        }
        return null;
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

    @Override
    default int compareTo(IExposedService o){
        return o.priority()-this.priority();
    }
}

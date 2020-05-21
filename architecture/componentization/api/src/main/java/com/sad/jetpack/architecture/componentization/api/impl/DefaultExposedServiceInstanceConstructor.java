package com.sad.jetpack.architecture.componentization.api.impl;

import com.sad.jetpack.architecture.componentization.api.ExposdServiceRelationMappingEntity;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceInstanceConstructor;

import java.lang.reflect.Constructor;

public class DefaultExposedServiceInstanceConstructor implements IExposedServiceInstanceConstructor {
    //1w 5-10 5 2 1/元
    //2、30w 1000/天
    //

    public Class cls;
    public Class[] classes;
    public Object[] objects;

    public DefaultExposedServiceInstanceConstructor(Class cls){
        this.cls=cls;
    }

    @Override
    public IExposedServiceInstanceConstructor constructorClass(Class... classes) {
        this.classes=classes;
        return this;
    }

    @Override
    public IExposedServiceInstanceConstructor constructorParameters(Object... objects) {
        this.objects=objects;
        return this;
    }

    @Override
    public <T> T instance() {
        if (this.cls!=null){
            try {
                Constructor<T> constructor=null;
                if (classes==null || objects==null || classes.length==0 || objects.length==0){
                    constructor=cls.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    return constructor.newInstance();
                }
                else {
                    if (classes.length==objects.length){
                        constructor=cls.getConstructor(classes);
                        constructor.setAccessible(true);
                        return constructor.newInstance(objects);
                    }

                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        return null;
    }
}

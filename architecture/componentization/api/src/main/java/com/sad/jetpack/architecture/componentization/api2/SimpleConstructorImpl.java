package com.sad.jetpack.architecture.componentization.api2;

import java.lang.reflect.Constructor;

public class SimpleConstructorImpl implements IConstructor {

    private Class[] classes;
    private Object[] objects;
    private Class cls;

    private SimpleConstructorImpl(Class cls){
        this.cls=cls;
    }

    public static SimpleConstructorImpl from(Class cls){
        return new SimpleConstructorImpl(cls);
    }

    public SimpleConstructorImpl classes(Class[] classes){
        this.classes=classes;
        return this;
    }

    public SimpleConstructorImpl objects(Object[] objects){
        this.objects=objects;
        return this;
    }


    @Override
    public <T> T instance() throws Exception {
        try {
            Constructor<T> constructor=null;
            if ((classes==null && objects==null) || (classes.length==0 && objects.length==0)){
                return new DefaultConstructor(cls).instance();
            }
            else {

                if (classes.length==objects.length){
                    constructor=cls.getConstructor(classes);
                    constructor.setAccessible(true);
                    return constructor.newInstance(objects);
                }
                else {
                    throw new Exception("The iconstructor u gave is invalid,maybe its lengths of the 'classes' and 'objects' is different !!!");
                }

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

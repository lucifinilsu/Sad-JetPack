package com.sad.jetpack.architecture.componentization.api2;

import java.lang.reflect.Constructor;

public class DefaultConstructor implements IConstructor {

    private Class cls;

    public DefaultConstructor(Class cls) {
        this.cls = cls;
    }

    @Override
    public <T> T instance() throws Exception {
        Constructor<T> constructor=cls.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}

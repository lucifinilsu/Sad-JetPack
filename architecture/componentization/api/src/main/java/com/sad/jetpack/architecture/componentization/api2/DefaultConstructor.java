package com.sad.jetpack.architecture.componentization.api2;

import java.lang.reflect.Constructor;

public class DefaultConstructor implements IConstructor {

    @Override
    public <T> T instance(Class<T> cls) throws Exception {
        Constructor<T> constructor=cls.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}

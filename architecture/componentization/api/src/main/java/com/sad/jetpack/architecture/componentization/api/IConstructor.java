package com.sad.jetpack.architecture.componentization.api;

public interface IConstructor {

    <T> T instance(Class<T> cls) throws Exception;
}

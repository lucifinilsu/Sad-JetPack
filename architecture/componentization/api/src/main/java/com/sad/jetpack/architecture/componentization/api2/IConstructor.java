package com.sad.jetpack.architecture.componentization.api2;

public interface IConstructor {

    <T> T instance() throws Exception;
}

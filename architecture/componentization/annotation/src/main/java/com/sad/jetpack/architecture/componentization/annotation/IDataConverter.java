package com.sad.jetpack.architecture.componentization.annotation;

public interface IDataConverter {

    <T> T convert(Object o);
}

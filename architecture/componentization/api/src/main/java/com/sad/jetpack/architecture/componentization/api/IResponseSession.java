package com.sad.jetpack.architecture.componentization.api;

public interface IResponseSession {

    boolean postResponseData(IBody body);

    boolean postResponseData(IResponse response);

}

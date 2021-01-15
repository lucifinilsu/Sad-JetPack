package com.sad.jetpack.architecture.componentization.api;

public interface IResponseSession {

    boolean postResponseData(IBody body,boolean intercepted);

    boolean postResponseData(IResponse response,boolean intercepted);

    default boolean postResponseData(IBody body){
        return postResponseData(body,false);
    };

    default boolean postResponseData(IResponse response){
        return postResponseData(response,false);
    }

    void throwException(Throwable throwable,Object extra);

}

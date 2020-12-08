package com.sad.jetpack.architecture.componentization.api;

public interface IResponseSession {

    boolean postResponseData(IDataContainer dataContainer);

    boolean postResponseData(IResponse response);

}

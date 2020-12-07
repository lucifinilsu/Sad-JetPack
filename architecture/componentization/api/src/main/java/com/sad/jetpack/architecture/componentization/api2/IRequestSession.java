package com.sad.jetpack.architecture.componentization.api2;

public interface IRequestSession {

    boolean replyRequestData(IDataContainer dataContainer,ICallerConfig callerConfig);

    default boolean replyRequestData(IDataContainer dataContainer){return replyRequestData(dataContainer, null);}

    boolean replyRequest(IRequest request,ICallerConfig callerConfig);

    default boolean replyRequest(IRequest request){
        return replyRequest(request,null);
    };

}

package com.sad.jetpack.architecture.componentization.api;

public interface IRequestSession {

    boolean replyRequestData(IBody body,ICallerConfig callerConfig);

    default boolean replyRequestData(IBody body){return replyRequestData(body, null);}

    boolean replyRequest(IRequest request,ICallerConfig callerConfig);

    default boolean replyRequest(IRequest request){
        return replyRequest(request,null);
    };

}

package com.sad.jetpack.architecture.componentization.api2;

public interface IComponentCallListener {

    default IRequest onComponentInputRequest(IRequest request,String componentId){
        return request;
    }

    boolean onComponentReceivedResponse(IResponse response, IRequestSession session, String componentId);

    void onComponentException(IRequest request, Throwable throwable,String componentId);
}

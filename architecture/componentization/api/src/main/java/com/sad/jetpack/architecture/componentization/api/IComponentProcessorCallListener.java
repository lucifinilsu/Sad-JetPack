package com.sad.jetpack.architecture.componentization.api;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

public interface IComponentProcessorCallListener{

    default IRequest onProcessorInputRequest(IRequest request,String processorId){
        return request;
    }

    boolean onProcessorReceivedResponse(IResponse response,String processorId);

    IResponse onProcessorMergeResponses(ConcurrentLinkedHashMap<IResponse,String> responses,String processorId);

    void onProcessorException(IRequest request, Throwable throwable,String processorId);

    default IRequest onChildComponentInputRequest(IRequest request,String componentId){
        return request;
    }

    boolean onChildComponentReceivedResponse(IResponse response, IRequestSession session, String componentId);

    void onChildComponentException(IRequest request, Throwable throwable,String componentId);

    default IRequest onChildProcessorInputRequest(IRequest request,String childProcessorId){
        return request;
    }

    boolean onChildProcessorReceivedResponse(IResponse response,String childProcessorId);

    default IResponse onChildProcessorMergeResponses(ConcurrentLinkedHashMap<IResponse,String> responses,IResponse childResponse,String childProcessorId){
        return childResponse;
    }

    void onChildProcessorException(IRequest request, Throwable throwable,String childProcessorId);
}

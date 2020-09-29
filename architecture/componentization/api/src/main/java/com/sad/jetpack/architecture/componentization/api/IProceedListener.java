package com.sad.jetpack.architecture.componentization.api;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import java.io.Serializable;

public interface IProceedListener extends Serializable {

    default IDataCarrier onInput(IDataCarrier inputData){
        return inputData;
    }
    default boolean onProceed(IDataCarrier data, IPCSession session, String messengerProxyId){
        return false;
    }
    default void onOutput(ConcurrentLinkedHashMap<String,IDataCarrier> outPutData){

    }
    default void onOutput(IDataCarrier totalOutputData){

    }
    default void onExceptionInPerformer(Throwable throwable){

    }
    default void onIntercepted(IPerformer performer,IExposedService lastExposedService,IDataCarrier outputData){

    }

}

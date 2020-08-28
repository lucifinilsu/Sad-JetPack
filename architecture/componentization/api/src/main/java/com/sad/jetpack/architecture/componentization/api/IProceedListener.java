package com.sad.jetpack.architecture.componentization.api;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

public interface IProceedListener {

    default <T> T onInput(IDataCarrier inputData){
        return (T) inputData;
    };
    default boolean onProceed(IPerformer performer, IDataCarrier data, IPCSession session, String messengerProxyId){
        return false;
    }
    default void onOutput(ConcurrentLinkedHashMap<String,IDataCarrier> outPutData){

    };
    default void onOutput(IDataCarrier totalOutputData){

    };
    default void onExceptionInPerformer(Throwable throwable){}
    default void onIntercepted(IPerformer performer,IExposedService lastExposedService,IDataCarrier outputData){

    };

}

package com.sad.jetpack.architecture.componentization.api;

public interface ICallerListener {

    default <T> T onStartToProceedExposedServiceGroup(IDataCarrier inputData){
        return (T) inputData;
    };
    default boolean onProceedExposedService(IPerformer performer,IDataCarrier data, IPCSession session,String messengerProxyId){
        return false;
    }
    default void onEndExposedServiceGroup(IDataCarrier outputData){

    };
    default void onFailureExposedServiceGroup(IDataCarrier currDataCarrier,Throwable throwable){}
    default void onIntercepted(IPerformer performer,IExposedService lastExposedService,IDataCarrier outputData){

    };

}

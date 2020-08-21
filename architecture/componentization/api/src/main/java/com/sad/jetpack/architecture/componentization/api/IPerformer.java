package com.sad.jetpack.architecture.componentization.api;

public interface IPerformer {

    void start(IDataCarrier data,boolean restart,long delay);

    default void start(IDataCarrier data){start(data,false,0);}

}

package com.sad.jetpack.architecture.componentization.api;

public interface IPCSession {

    //boolean openChat(IPCMessenger messenger, D message);

    default String sessionId(){return hashCode()+"";};

    boolean componentChat(IDataCarrier o,IPCMessenger messenger);


}

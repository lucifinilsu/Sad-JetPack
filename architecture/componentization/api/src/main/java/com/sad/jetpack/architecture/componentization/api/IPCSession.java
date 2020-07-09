package com.sad.jetpack.architecture.componentization.api;
@Deprecated
public interface IPCSession<D> {

    //boolean openChat(IPCMessenger messenger, D message);

    String sessionId();

    boolean onChat(IPCMessenger messenger);

}

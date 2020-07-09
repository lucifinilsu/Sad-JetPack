package com.sad.jetpack.architecture.componentization.api;

public interface IPCMessenger {

    boolean reply(Object d);

    String messengerId();

    <T> T getMessage();

}

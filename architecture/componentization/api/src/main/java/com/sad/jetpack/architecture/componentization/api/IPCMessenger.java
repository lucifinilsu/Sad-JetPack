package com.sad.jetpack.architecture.componentization.api;

public interface IPCMessenger {

    default boolean reply(Object d){return reply(d,null);}

    default boolean reply(Object d,IPCSession session){return false;};

    default String messengerId(){return hashCode()+"";};

    default <T> T extraMessage(){return null;};

}

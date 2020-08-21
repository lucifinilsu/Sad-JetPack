package com.sad.jetpack.architecture.componentization.api;

public interface IPCMessenger {

    default boolean reply(IDataCarrier d){return reply(d,null);}

    default boolean reply(IDataCarrier d,IPCSession session){return false;};

    default String messengerId(){return hashCode()+"";};

    default IDataCarrier extraMessage(){return null;};

}

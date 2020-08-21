package com.sad.jetpack.architecture.componentization.api.impl;

import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.architecture.componentization.api.IPCMessenger;
import com.sad.jetpack.architecture.componentization.api.IPCSession;

public abstract class AbsIPCMessenger<A extends AbsIPCMessenger<A>> implements IPCMessenger {
    private String id="";
    private IDataCarrier extraMessage;
    private AbsIPCMessenger(){}
    public AbsIPCMessenger(String id){
        this.id=id;
    }

    @Override
    public IDataCarrier extraMessage() {
        return extraMessage;
    }

    @Override
    public String messengerId() {
        return this.id;
    }

    public A extraMessage(IDataCarrier extraMessage){
        this.extraMessage=extraMessage;
        return (A) this;
    }

    @Override
    public abstract boolean reply(IDataCarrier d, IPCSession session);
}

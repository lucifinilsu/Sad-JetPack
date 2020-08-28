package com.sad.jetpack.architecture.componentization.api.impl;

import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.architecture.componentization.api.IPCMessenger;

public class DefaultIPCMessenger implements IPCMessenger {
    private String id="";
    private IDataCarrier extraMessage;
    private DefaultIPCMessenger(){}
    public DefaultIPCMessenger(String id){
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

    public DefaultIPCMessenger extraMessage(IDataCarrier extraMessage){
        this.extraMessage=extraMessage;
        return this;
    }

}

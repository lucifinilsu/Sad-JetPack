package com.sad.jetpack.architecture.componentization.api.internal;

import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.architecture.componentization.api.IPCMessenger;
import com.sad.jetpack.architecture.componentization.api.IPCSession;
import com.sad.jetpack.architecture.componentization.api.impl.AbsIPCMessenger;

public class InternalMessenger extends AbsIPCMessenger<InternalMessenger> {
    private IPCMessenger realMessenger;
    public InternalMessenger(String id) {
        super(id);
    }

    public InternalMessenger(IPCMessenger messenger){
        super(messenger.messengerId());
        this.realMessenger=messenger;
    }

    @Override
    public boolean reply(IDataCarrier d, IPCSession session) {
        return false;
    }
}

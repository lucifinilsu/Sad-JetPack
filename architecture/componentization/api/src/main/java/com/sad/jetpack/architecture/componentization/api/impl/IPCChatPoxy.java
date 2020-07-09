package com.sad.jetpack.architecture.componentization.api.impl;

import com.sad.jetpack.architecture.componentization.api.IExposedService;

public abstract class IPCChatPoxy<H> implements IExposedService{
    private H host;
    public IPCChatPoxy(H host){
        this.host=host;
    }

    public H getHost() {
        return host;
    }
}

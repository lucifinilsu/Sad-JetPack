package com.sad.jetpack.architecture.componentization.api.impl;

import com.sad.jetpack.architecture.componentization.annotation.ExposedService;
import com.sad.jetpack.architecture.componentization.annotation.IPCChat;
import com.sad.jetpack.architecture.componentization.api.IExposedService;

import java.lang.annotation.Annotation;

public abstract class IPCChatProxy<H> implements IExposedService{

    private H host;
    private IPCChat chat;
    public IPCChatProxy(H host,IPCChat chat){
        this.host=host;
        this.chat=chat;
    }

    public H getHost() {
        return host;
    }

    @Override
    public ExposedService info() {
        return new ExposedService(){
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String[] assetsDir() {
                return chat.assetsDir();
            }

            @Override
            public String[] url() {
                return chat.url();
            }

            @Override
            public String description() {
                return chat.description();
            }

            @Override
            public boolean asyncWorker() {
                return false;
            }
        };
    }
}

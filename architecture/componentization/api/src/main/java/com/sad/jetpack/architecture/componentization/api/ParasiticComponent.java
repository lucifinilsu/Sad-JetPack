package com.sad.jetpack.architecture.componentization.api;

import com.sad.jetpack.architecture.componentization.annotation.Component;
import com.sad.jetpack.architecture.componentization.annotation.IPCChat;

import java.lang.annotation.Annotation;

public abstract class ParasiticComponent<H> implements IComponent {

    private H host;
    private IPCChat chat;
    public H getHost() {
        return host;
    }
    public ParasiticComponent(H host, IPCChat chat){
        this.host=host;
        this.chat=chat;
    }


    @Override
    public Component info() {
        return new Component(){
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
            public int version() {
                return chat.version();
            }
        };
    }
}

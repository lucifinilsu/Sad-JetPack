package com.sad.jetpack.architecture.componentization.api;

import com.sad.jetpack.architecture.componentization.annotation.IPCChat;

import java.lang.reflect.Constructor;

final class ParasiticComponentFromHostConstructor implements IConstructor {
    private Object host;
    private IPCChat chat;
    protected ParasiticComponentFromHostConstructor(Object host,IPCChat chat){
        this.host=host;
        this.chat=chat;
    }
    @Override
    public <T> T instance(Class<T> cls) throws Exception {
        Constructor constructor = null;
        try {
            constructor = cls.getDeclaredConstructor(cls, IPCChat.class);
        } catch (Exception e) {
            e.printStackTrace();
            constructor = cls.getConstructor(cls, IPCChat.class);
        }
        constructor.setAccessible(true);
        IComponent parasiticComponent = (IComponent) constructor.newInstance(host, chat);
        return (T) parasiticComponent;
    }
}

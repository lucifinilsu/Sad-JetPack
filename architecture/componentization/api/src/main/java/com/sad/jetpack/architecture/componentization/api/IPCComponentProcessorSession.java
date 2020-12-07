package com.sad.jetpack.architecture.componentization.api;

import android.os.Message;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

public interface IPCComponentProcessorSession extends IPCExceptionListener{

    default Message onProcessorInput(String processorId, Message message){
        return message;
    }

    void onProcessorOutput(String processorId, Message message);

    void onProcessorGenerate(ConcurrentLinkedHashMap<Message,String> messages);

    void onComponentChat(String curl,Message message);
}

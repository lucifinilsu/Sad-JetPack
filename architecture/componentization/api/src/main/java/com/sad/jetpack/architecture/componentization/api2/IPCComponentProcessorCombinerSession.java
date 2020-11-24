package com.sad.jetpack.architecture.componentization.api2;

import android.os.Message;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import java.util.concurrent.ConcurrentLinkedDeque;

public interface IPCComponentProcessorCombinerSession extends IPCExceptionListener{

    default Message onProcessorCombinerInput(Message message){
        return message;
    }

    void onProcessorChat(String url, Message message);

    void onProcessorCombinerOutput(String cid,Message message);

    void onProcessorCombinerOutput(ConcurrentLinkedHashMap<Message,String> messages);


}

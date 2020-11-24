package com.sad.jetpack.architecture.componentization.api2;

import android.os.Message;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

public interface ISequenceMessageBoundaryInterceptor {

    default Message handleMessage(Message message){
        return message;
    }
}

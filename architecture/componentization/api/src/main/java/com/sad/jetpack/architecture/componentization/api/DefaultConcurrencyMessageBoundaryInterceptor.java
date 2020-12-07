package com.sad.jetpack.architecture.componentization.api;

import android.os.Message;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

public class DefaultConcurrencyMessageBoundaryInterceptor implements IConcurrencyMessageBoundaryInterceptor {
    private DefaultConcurrencyMessageBoundaryInterceptor(){}
    public static IConcurrencyMessageBoundaryInterceptor getInstance(){
        return new DefaultConcurrencyMessageBoundaryInterceptor();
    }

    @Override
    public Message handleMessages(ConcurrentLinkedHashMap<Message, String> messages) {
        return null;
    }
}

package com.sad.jetpack.architecture.componentization.api2;

import android.os.Message;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import java.util.concurrent.ConcurrentLinkedDeque;

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

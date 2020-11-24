package com.sad.jetpack.architecture.componentization.api2;

import android.os.Message;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import java.util.concurrent.ConcurrentLinkedDeque;

public interface IConcurrencyMessageBoundaryInterceptor {

    Message handleMessages(ConcurrentLinkedHashMap<Message, String> messages);
}

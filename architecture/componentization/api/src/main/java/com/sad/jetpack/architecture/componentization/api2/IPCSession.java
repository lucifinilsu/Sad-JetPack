package com.sad.jetpack.architecture.componentization.api2;

import android.os.Message;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

public interface IPCSession {

    void onComponentChat(Message message);

    void onInput(Message message);

    void onOutput(ConcurrentLinkedHashMap<String,Message> messages);

    void onOutput(Message message);

    void onExceptionInPerformer(Throwable throwable);
}

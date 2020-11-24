package com.sad.jetpack.architecture.componentization.api2;


import android.os.Message;

import java.util.LinkedHashMap;

public interface IComponentProcessor<I extends IComponentProcessor<I>> {

    String processorId();

    I processorSession(IPCComponentProcessorSession processorSession);

    IPCComponentProcessorSession processorSession();

    long timeout();

    long delay();

    void submit(Message message);

    I join(IComponent component,String curl);

    I join(LinkedHashMap<IComponent,String> components);

    I join(IComponentRepository repository);

    I join(IComponentProcessor sequenceProcessor);
}

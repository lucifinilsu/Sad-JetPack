package com.sad.jetpack.architecture.componentization.api;
import java.util.LinkedHashMap;

public interface IProcessor {

    LinkedHashMap<Object,String> extraObjectInstances();

    LinkedHashMap<IExposedService,String> exposedServiceInstance();

    IProcessor timeout(long timeout);

    IPerformer submit();

    IProcessor listener(IProceedListener callerListener);
}

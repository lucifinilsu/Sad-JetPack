package com.sad.jetpack.architecture.componentization.api;
import java.util.LinkedHashMap;
import java.util.List;
public interface IProcessor {

    LinkedHashMap<Object,String> extraObjectInstances();

    LinkedHashMap<IExposedService,String> exposedServiceInstance();

    IProcessor timeout(long timeout);

    IPerformer submit();

    IProcessor listener(ICallerListener callerListener);
}

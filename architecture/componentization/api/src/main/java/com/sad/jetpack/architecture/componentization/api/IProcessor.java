package com.sad.jetpack.architecture.componentization.api;
import java.util.List;
public interface IProcessor {

    List extraObjectInstances();

    List<IExposedService> exposedServiceInstance();

    IProcessor timeout(long timeout);

    IPerformer submit();

    IProcessor listener(ICallerListener callerListener);
}

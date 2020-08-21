package com.sad.jetpack.architecture.componentization.api;
import java.util.List;
public interface ICaller {

    List extraObjectInstances();

    List<IExposedService> exposedServiceInstance();

    ICaller timeout(long timeout);

    IPerformer submit();

    ICaller listener(ICallerListener callerListener);
}

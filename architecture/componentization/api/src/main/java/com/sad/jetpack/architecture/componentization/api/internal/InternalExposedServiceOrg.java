package com.sad.jetpack.architecture.componentization.api.internal;

import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IPCMessenger;
@Deprecated
public class InternalExposedServiceOrg implements IExposedService {
    @Override
    public <T> T action(IPCMessenger messenger) {
        return null;
    }
}

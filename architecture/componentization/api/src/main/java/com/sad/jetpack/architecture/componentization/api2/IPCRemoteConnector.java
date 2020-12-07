package com.sad.jetpack.architecture.componentization.api2;

public interface IPCRemoteConnector {

    IPCRemoteCallListener listener();

    ICallerConfig callerConfig();

    @RemoteAction int action();

    void sendRequest(IRequest request,ITarget target) throws Exception;

    Builder toBuilder();

    interface Builder{

        Builder listener(IPCRemoteCallListener listener);

        Builder action(@RemoteAction int action);

        Builder callerConfig(ICallerConfig callerConfig);

        IPCRemoteConnector build();

    }
}

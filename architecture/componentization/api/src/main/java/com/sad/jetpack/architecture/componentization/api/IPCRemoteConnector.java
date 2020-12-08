package com.sad.jetpack.architecture.componentization.api;

import android.os.Messenger;

public interface IPCRemoteConnector {

    IRequest request();

    ITarget target();

    IPCRemoteCallListener listener();

    ICallerConfig callerConfig();

    @RemoteAction int action();

    Messenger replyMessenger();

    void execute() throws Exception;

    Builder toBuilder();

    interface Builder{

        Builder request(IRequest request);

        Builder target(ITarget target);

        Builder listener(IPCRemoteCallListener listener);

        Builder action(@RemoteAction int action);

        Builder replyMessenger(Messenger replyMessenger);

        Builder callerConfig(ICallerConfig callerConfig);

        IPCRemoteConnector build();

    }
}

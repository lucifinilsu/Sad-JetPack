package com.sad.jetpack.architecture.componentization.api2;

import android.content.ComponentName;
import android.os.Messenger;

public interface IPCRemoteCallListener {

    default IRequest onRemoteCallInputRequest(IRequest request,ITarget target){
        return request;
    }

    default void onRemoteCallConnectedServer(ComponentName name, Messenger serverMessenger,ITarget target){}

    boolean onRemoteCallReceivedResponse(IResponse response, IRequestSession session, ITarget target);

    void onRemoteCallException(IRequest request, Throwable throwable,ITarget target);
}

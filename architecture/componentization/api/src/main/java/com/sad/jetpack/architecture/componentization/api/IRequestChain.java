package com.sad.jetpack.architecture.componentization.api;

import java.io.Serializable;
@Deprecated
public interface IRequestChain extends Serializable {

    void proceedRequest(IPCMessenger messenger)  throws Exception;

}

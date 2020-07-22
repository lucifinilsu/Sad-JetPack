package com.sad.jetpack.architecture.componentization.api;

import java.io.Serializable;

public interface IRequestChain extends Serializable {

    void proceedRequest(IComponentRequest request)  throws Exception;

}

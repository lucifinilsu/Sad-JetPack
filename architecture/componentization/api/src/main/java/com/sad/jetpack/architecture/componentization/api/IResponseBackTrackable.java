package com.sad.jetpack.architecture.componentization.api;

public interface IResponseBackTrackable {

    default void onBackTrackResponse(IComponentChain chain) throws Exception{
        chain.proceedResponse();
    }

}

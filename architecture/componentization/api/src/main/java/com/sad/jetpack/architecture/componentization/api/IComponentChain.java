package com.sad.jetpack.architecture.componentization.api;

public interface IComponentChain {

    IResponse response();

    String parentId();

    void proceedResponse(IResponse response,String groupId) throws Exception;

    default void proceedResponse() throws Exception{
        proceedResponse(response(), parentId());
    }

    interface IComponentChainTerminalCallback{

        void onLast(IResponse response,String id,boolean intercepted) throws Exception;

    }

}

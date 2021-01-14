package com.sad.jetpack.demo.test;

import androidx.annotation.NonNull;

import com.sad.jetpack.architecture.componentization.annotation.Component;
import com.sad.jetpack.architecture.componentization.api.IComponent;
import com.sad.jetpack.architecture.componentization.api.IComponentChain;
import com.sad.jetpack.architecture.componentization.api.IRequest;
import com.sad.jetpack.architecture.componentization.api.IResponseSession;
import com.sad.jetpack.architecture.componentization.api.LogcatUtils;
import com.sad.jetpack.architecture.componentization.api.ResponseImpl;

@Component(url = "test://atsc/1",description = "回溯链成员1")
public class TestStaticComponent1 implements IComponent {

    @NonNull
    @Override
    public String toString() {
        return description();
    }

    @Override
    public void onCall(IRequest request, IResponseSession session) throws Exception {
        session.postResponseData(ResponseImpl.newBuilder().request(request).build());
    }

    @Override
    public int priority() {
        return 999;
    }

    @Override
    public void onBackTrackResponse(IComponentChain chain) throws Exception {
        LogcatUtils.e(">>>回溯链响应1："+chain.response());
        chain.proceedResponse();
    }
}

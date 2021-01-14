package com.sad.jetpack.demo.test;

import androidx.annotation.NonNull;

import com.sad.jetpack.architecture.componentization.annotation.Component;
import com.sad.jetpack.architecture.componentization.api.IComponent;
import com.sad.jetpack.architecture.componentization.api.IComponentChain;
import com.sad.jetpack.architecture.componentization.api.IRequest;
import com.sad.jetpack.architecture.componentization.api.IResponseSession;
import com.sad.jetpack.architecture.componentization.api.LogcatUtils;
import com.sad.jetpack.architecture.componentization.api.ResponseImpl;

@Component(url = "test://tsc/2",description = "回溯链成员2")
public class TestStaticComponent2 implements IComponent {

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
    public void onBackTrackResponse(IComponentChain chain) throws Exception {
        LogcatUtils.e(">>>回溯链响应2："+chain.response());
        chain.proceedResponse();
    }

    @Override
    public int priority() {
        return 888;
    }
}

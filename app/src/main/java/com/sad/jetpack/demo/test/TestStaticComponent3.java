package com.sad.jetpack.demo.test;

import androidx.annotation.NonNull;

import com.sad.jetpack.architecture.componentization.annotation.Component;
import com.sad.jetpack.architecture.componentization.api.IComponent;
import com.sad.jetpack.architecture.componentization.api.IComponentChain;
import com.sad.jetpack.architecture.componentization.api.IRequest;
import com.sad.jetpack.architecture.componentization.api.IResponseSession;
import com.sad.jetpack.architecture.componentization.api.LogcatUtils;
import com.sad.jetpack.architecture.componentization.api.ResponseImpl;

@Component(url = "test://tsc/3",description = "回溯链成员3")
public class TestStaticComponent3 implements IComponent {

    @Override
    public void onCall(IRequest request, IResponseSession session) throws Exception {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(3000);
                    session.postResponseData(ResponseImpl.newBuilder().request(request).build());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

    @NonNull
    @Override
    public String toString() {
        return description();
    }

    @Override
    public int priority() {
        return 777;
    }

    @Override
    public void onBackTrackResponse(IComponentChain chain) throws Exception {
        LogcatUtils.e(">>>回溯链响应3："+chain.response());
        chain.proceedResponse();
    }
}

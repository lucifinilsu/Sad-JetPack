package com.sad.jetpack.test.module1;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.widget.Toast;

import com.sad.jetpack.architecture.appgo.annotation.ApplicationLifeCycleAction;
import com.sad.jetpack.architecture.appgo.api.AppGo;
import com.sad.jetpack.architecture.appgo.api.IApplicationLifecyclesObserver;
import com.sad.jetpack.architecture.componentization.annotation.Component;
import com.sad.jetpack.architecture.componentization.api.DefaultDataContainer;
import com.sad.jetpack.architecture.componentization.api.IComponent;
import com.sad.jetpack.architecture.componentization.api.IRequest;
import com.sad.jetpack.architecture.componentization.api.IResponseSession;

import static com.sad.jetpack.test.module1.TestInitModule1.path;

@Component(url =path)
public class TestInitModule1 implements IApplicationLifecyclesObserver, IComponent {
    protected static final String path="xxx://ssss.php.cn/java/base7/index?dww=cs";
    @ApplicationLifeCycleAction(priority = 1561)
    @Override
    public void onApplicationConfigurationChanged(Application application, Configuration newConfig) {

    }
    @ApplicationLifeCycleAction(priority = 1561)
    @Override
    public void attachApplicationBaseContext(Application application,Context base) {

    }
    @Override
    public void onCall(IRequest request, IResponseSession session) throws Exception {
        Toast.makeText(AppGo.get().getContext(),"服务调用",Toast.LENGTH_LONG).show();
        session.postResponseData(DefaultDataContainer.newIntance().put("result","老大，我的工作做完了"));
    }


    //keytool -genkey -alias demoapp -keypass 123456 -keyalg RSA -keysize 2048 -validity 36500 -keystore D:\workspace\project\SAD\SAD-PROJECT\sad-JetPack\app\appkey.jks -storepass 123456
}

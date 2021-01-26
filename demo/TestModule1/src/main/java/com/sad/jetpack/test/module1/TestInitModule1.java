package com.sad.jetpack.test.module1;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sad.jetpack.architecture.appgo.annotation.ApplicationLifeCycleAction;
import com.sad.jetpack.architecture.appgo.api.IApplicationLifecyclesObserver;
import com.sad.jetpack.architecture.componentization.annotation.Component;
import com.sad.jetpack.architecture.componentization.api.BodyImpl;
import com.sad.jetpack.architecture.componentization.api.DefaultDataContainer;
import com.sad.jetpack.architecture.componentization.api.IComponent;
import com.sad.jetpack.architecture.componentization.api.IDataContainer;
import com.sad.jetpack.architecture.componentization.api.IRequest;
import com.sad.jetpack.architecture.componentization.api.IResponseSession;
import com.sad.jetpack.architecture.componentization.api.LogcatUtils;
import com.sad.jetpack.architecture.componentization.api.SCore;

import static com.sad.jetpack.test.module1.TestInitModule1.path;

@Component(url =path)
public class TestInitModule1 implements IApplicationLifecyclesObserver, IComponent {
    protected static final String path="xxx://ssss.php.cn/java/base7/index?dww=cs";

    @ApplicationLifeCycleAction(priority = 9999)
    @Override
    public void onApplicationPreCreated(Application application) {
        SCore.enableLogUtils(true);
        SCore.enableInternalLog(false);
        SCore.initIPC(application);
        LogcatUtils.e(">>> 666666666666666666666");
    }

    @Override
    public void onApplicationCreated(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }

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
        //Toast.makeText(AppGo.get().getContext(),"服务调用",Toast.LENGTH_LONG).show();


        IDataContainer dataContainer=request.body().dataContainer();
        if (dataContainer!=null){
            LogcatUtils.e("---->收到请求信息："+request.body().dataContainer().getMap());
            if (dataContainer.getMap().containsKey("remote")){
                LogcatUtils.e("---->远端回送信息："+dataContainer.getMap());
            }
            else {
                session.postResponseData(BodyImpl.newBuilder().dataContainer(DefaultDataContainer.newIntance().put("result","老大，我的工作做完了")).build());
            }
        }
        else {
            session.postResponseData(BodyImpl.newBuilder().dataContainer(DefaultDataContainer.newIntance().put("result","老大，我的工作做完了")).build());
        }
    }


    //keytool -genkey -alias demoapp -keypass 123456 -keyalg RSA -keysize 2048 -validity 36500 -keystore D:\workspace\project\SAD\SAD-PROJECT\sad-JetPack\app\appkey.jks -storepass 123456
}

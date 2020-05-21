package com.sad.jetpack.test.module1;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.FileUtils;
import android.widget.Toast;

import com.sad.jetpack.architecture.appgo.annotation.ApplicationLifeCycleAction;
import com.sad.jetpack.architecture.appgo.api.AppGo;
import com.sad.jetpack.architecture.appgo.api.IApplicationLifecyclesObserver;
import com.sad.jetpack.architecture.componentization.annotation.ExposedService;
import com.sad.jetpack.architecture.componentization.api.IExposedActionNotifier;
import com.sad.jetpack.architecture.componentization.api.IExposedService;

import java.io.File;

import static com.sad.jetpack.test.module1.TestInitModule1.path;

@ExposedService(url =path)
public class TestInitModule1 implements IApplicationLifecyclesObserver, IExposedService {
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
    public <T> T action(IExposedActionNotifier notifier, Object... params) {
        Toast.makeText(AppGo.get().getContext(),"服务调用",Toast.LENGTH_LONG).show();
        notifier.notifyBy("模块下服务被调用");
        return null;
    }
}

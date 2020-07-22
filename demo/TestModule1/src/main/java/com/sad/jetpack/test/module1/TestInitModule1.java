package com.sad.jetpack.test.module1;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.widget.Toast;

import com.sad.jetpack.architecture.appgo.annotation.ApplicationLifeCycleAction;
import com.sad.jetpack.architecture.appgo.api.AppGo;
import com.sad.jetpack.architecture.appgo.api.IApplicationLifecyclesObserver;
import com.sad.jetpack.architecture.componentization.annotation.ExposedService;
import com.sad.jetpack.architecture.componentization.api.IPCMessenger;
import com.sad.jetpack.architecture.componentization.api.IPCSession;
import com.sad.jetpack.architecture.componentization.api.IExposedService;

import static com.sad.jetpack.test.module1.TestInitModule1.path;

@ExposedService(url =path)
public class TestInitModule1 implements IApplicationLifecyclesObserver, IExposedService,IPCSession {
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
    public <T> T action(IPCMessenger messenger) {
        Toast.makeText(AppGo.get().getContext(),"服务调用",Toast.LENGTH_LONG).show();
        messenger.reply("老大，我的工作做完了", this);

       /* session.openChat(new IPCMessenger() {
            @Override
            public boolean reply(IPCSession session, Object o) {
                Log.e("sad-jetpack","------------->总部回复:"+o);
                session.openChat(this,"不要再回复了");
                return false;
            }
        },"模块下服务被调用");*/

        /*notifier.chat(new IPCSession() {
            @Override
            public boolean chat(IPCMessenger messenger, Object o) {

                return false;
            }
        }, "模块下服务被调用");*/
        return null;
    }

    @Override
    public boolean componentChat(Object o,IPCMessenger messenger) {
        return false;
    }

    //keytool -genkey -alias demoapp -keypass 123456 -keyalg RSA -keysize 2048 -validity 36500 -keystore D:\workspace\project\SAD\SAD-PROJECT\sad-JetPack\app\appkey.jks -storepass 123456
}

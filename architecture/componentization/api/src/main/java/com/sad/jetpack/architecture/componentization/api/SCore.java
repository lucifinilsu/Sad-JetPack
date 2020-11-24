package com.sad.jetpack.architecture.componentization.api;

import android.content.Context;

import com.sad.jetpack.architecture.componentization.annotation.EncryptUtil;
import com.sad.jetpack.architecture.componentization.annotation.IPCChat;
import com.sad.jetpack.architecture.componentization.annotation.NameUtils;
import com.sad.jetpack.architecture.componentization.api.impl.IPCChatProxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class SCore {

    public static IExposedServiceManager getManager() {
        IExposedServiceManager exposedServiceManager = ExposedServiceManager.newInstance();
        return exposedServiceManager;
    }

    public static IExposedServiceManager getManager(Context context) {
        IExposedServiceManager exposedServiceManager = ExposedServiceManager.newInstance(context);
        return exposedServiceManager;
    }

    public static <O> void registerIPCHost(O host) {
        Class<?> cls = host.getClass();
        String hostClsName = cls.getCanonicalName();
        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods
        ) {
            IPCChat chat = method.getAnnotation(IPCChat.class);
            if (chat != null) {
                String[] urls = chat.url();
                for (String url : urls
                ) {
                    String proxyUrlEncrypt = EncryptUtil.getInstance().XORencode(url, "abc123");//ValidUtils.encryptMD5ToString(url);
                    String dynamicExposedServiceClsName = NameUtils.getParasiticComponentClassSimpleName(hostClsName + "." + method.getName(), proxyUrlEncrypt, "$$");
                    //生成实例对象
                    try {
                        String className = cls.getPackage().getName() + "." + dynamicExposedServiceClsName;
                        Class<IPCChatProxy<O>> dc = (Class<IPCChatProxy<O>>) Class.forName(className);
                        Constructor constructor = null;
                        try {
                            constructor = dc.getDeclaredConstructor(cls, IPCChat.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                            constructor = dc.getConstructor(cls, IPCChat.class);
                        }
                        constructor.setAccessible(true);
                        IPCChatProxy<O> dynamicComponent = (IPCChatProxy<O>) constructor.newInstance(host, chat);
                        //存入集合
                        ExposedServiceInstanceStorageManager.registerExposedServiceInstance(proxyUrlEncrypt + ExposedServiceInstanceStorageManager.PATH_KEY_SEPARATOR + method.getName() + ExposedServiceInstanceStorageManager.PATH_KEY_SEPARATOR + host.hashCode(), dynamicComponent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public static <O> void unregisterIPCHost(O host) {
        Class<?> cls = host.getClass();
        String hostClsName = cls.getCanonicalName();
        Method[] methods = cls.getDeclaredMethods();

        for (Method method : methods
        ) {
            IPCChat chat = method.getAnnotation(IPCChat.class);
            if (chat != null) {

                String[] urls = chat.url();
                for (String url : urls) {
                    String proxyUrlMD5 = EncryptUtil.getInstance().XORencode(url, "abc123");//ValidUtils.encryptMD5ToString(url);

                    try {
                        //Log.e("ipc","------------------->开始注销宿主"+hostClsName+"的事件接收器"+proxyUrlMD5);
                        ExposedServiceInstanceStorageManager.unregisterExposedServiceInstance(proxyUrlMD5 + ExposedServiceInstanceStorageManager.PATH_KEY_SEPARATOR + method.getName() + ExposedServiceInstanceStorageManager.PATH_KEY_SEPARATOR + host.hashCode());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

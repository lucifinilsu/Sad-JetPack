package com.sad.jetpack.architecture.componentization.api2;

import android.content.Context;

import com.sad.jetpack.architecture.componentization.annotation.EncryptUtil;
import com.sad.jetpack.architecture.componentization.annotation.IPCChat;
import com.sad.jetpack.architecture.componentization.annotation.NameUtils;

import java.lang.reflect.Method;

public class SCore {

    private SCore(){}

    public static IComponentsCluster getCluster(Context context){
        return new InternalComponentCluster(context);
    }
    public static IComponentsCluster getCluster(){
        return getCluster(InternalContextHolder.get().getContext());
    }

    public static <T> T getFirstInstance(String curl){
        return getFirstInstance(curl,null);
    }
    public static <T> T getFirstInstance(Context context,String curl){
        return getFirstInstance(context,curl,null);
    }
    public static <T> T getFirstInstance(String curl, IConstructor constructor){
        return getFirstInstance(InternalContextHolder.get().getContext(),curl,constructor);
    }
    public static <T> T getFirstInstance(Context context, String curl, IConstructor constructor){
        IComponentsCluster cluster=getCluster(context);
        return cluster
                .addConstructorToAll(constructor)
                .repository(curl)
                .firstInstance();
    }

    public static <T> T getFirstInstance(Context context, String curl, IConstructor constructor,Class<T> cls){
        IComponentsCluster cluster=getCluster(context);
        return cluster
                .addConstructorToAll(constructor)
                .repository(curl)
                .firstInstance();
    }
    public static IComponentCallable getComponentCallable(String curl){
        return getComponentCallable(curl,null);
    }
    public static IComponentCallable getComponentCallable(Context context, String curl){
        return getComponentCallable(context,curl,null);
    }
    public static IComponentCallable getComponentCallable(String curl, IConstructor constructor){
        return getComponentCallable(InternalContextHolder.get().getContext(),curl,constructor);
    }

    public static IComponentCallable getComponentCallable(Context context, String curl,IConstructor constructor){
        IComponentsCluster cluster=getCluster(context);
        IComponentCallable componentCallable = cluster
                .addConstructorToAll(constructor)
                .repository(curl)
                .firstComponentCallableInstance();
        return componentCallable;
    }
    
    public static <O> void registerParasiticComponentFromHost(O host){
        registerParasiticComponentFromHost(host,null);
    }

    public static void initIPC(Context context){
        try {
            IPCRemoteConnectorImpl.newBuilder(context)
                    .action(RemoteAction.REMOTE_ACTION_REGISTER_TO_MESSENGERS_POOL)
                    .build()
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static IPCRemoteConnector.Builder ipc(Context context){
        return IPCRemoteConnectorImpl.newBuilder(context);
    }

    public static IPCRemoteConnector.Builder ipc(){
        return IPCRemoteConnectorImpl.newBuilder(InternalContextHolder.get().getContext());
    }

    public static <O> void registerParasiticComponentFromHost(O host, IConstructor constructor) {
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
                    String parasiticComponentClassSimpleName = NameUtils.getParasiticComponentClassSimpleName(hostClsName + "." + method.getName(), proxyUrlEncrypt, "$$");
                    //生成实例对象
                    try {
                        String className = cls.getPackage().getName() + "." + parasiticComponentClassSimpleName;
                        Class<IComponent> dc = (Class<IComponent>) Class.forName(className);
                        if (constructor==null){
                            constructor=new ParasiticComponentFromHostConstructor(host,chat);
                        }
                        IComponent component=constructor.instance(dc);
                        IComponentCallable componentCallable=InternalComponentCallable.newBuilder(component)
                                .componentId(url)
                                .build()
                                ;
                        //存入集合
                        ParasiticComponentRepositoryFactory.registerParasiticComponent(host,componentCallable);
                        //ExposedServiceInstanceStorageManager.registerExposedServiceInstance(proxyUrlEncrypt + ExposedServiceInstanceStorageManager.PATH_KEY_SEPARATOR + method.getName() + ExposedServiceInstanceStorageManager.PATH_KEY_SEPARATOR + host.hashCode(), dynamicComponent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
    public static <O> void unregisterParasiticComponentFromHost(O host) {
        ParasiticComponentRepositoryFactory.unregisterParasiticComponent(host);
    }
}

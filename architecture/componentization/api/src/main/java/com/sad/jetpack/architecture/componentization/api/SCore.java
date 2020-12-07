package com.sad.jetpack.architecture.componentization.api;

import android.content.Context;
import android.os.Message;

import com.sad.jetpack.architecture.componentization.annotation.EncryptUtil;
import com.sad.jetpack.architecture.componentization.annotation.IPCChat;
import com.sad.jetpack.architecture.componentization.annotation.NameUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class SCore {

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
    public static <T> T getFirstInstance(String curl,IConstructor constructor){
        return getFirstInstance(InternalContextHolder.get().getContext(),curl,constructor);
    }

    public static <T> T getFirstInstance(Context context,String curl,IConstructor constructor){
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

    public static IComponentCallable getComponentCallable(Context context, String curl, IConstructor constructor){
        IComponentsCluster cluster=getCluster(context);
        IComponent component = cluster
                .addConstructorToAll(constructor)
                .repository(curl)
                .firstComponentInstance();
        return new InternalComponentSingleCallable(context,component);
    }

    public static void sequencePostMessageToCurrProcess(Context context,Message message,String url,IPCComponentProcessorSession session){
        IComponentsCluster cluster=getCluster(context).componentRepositoryFactory(new ParasiticComponentRepositoryFactory(context));
        // I drive my car to working
        ComponentProcessorBuilderImpl.newBuilder(url).asSequence().processorSession(session).join(cluster.repository(url)).submit(message);
    }

    public static void concurrencyPostMessageToCurrProcess(Context context,Message message,String url,IPCComponentProcessorSession session){
        IComponentsCluster cluster=getCluster(context).componentRepositoryFactory(new ParasiticComponentRepositoryFactory(context));
        // I drive my car to working
        ComponentProcessorBuilderImpl.newBuilder(url).asConcurrency().processorSession(session).join(cluster.repository(url)).submit(message);
    }

    public static void ipc(Context context,Message message,IPCTarget target,IPCResultCallback callback) throws Exception{
        IPCRemoteConnector.sendMessage(context,message,target,callback);
    }

    public static void initIPC(Context context) throws Exception {
        IPCRemoteConnector.registerMessengerToServer(context);
    }

    public static <O> void registerParasiticComponentFromHost(O host){
        registerParasiticComponentFromHost(host,null);
    }

    public static <O> void registerParasiticComponentFromHost(O host,IConstructor constructor) {
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
                        Class<IComponent> dc = (Class<IComponent>) Class.forName(className);
                        if (constructor!=null){
                            IComponent dynamicComponent=constructor.instance(dc);
                        }
                        if (constructor==null){
                            constructor=new ParasiticComponentFromHostConstructor(host,chat);
                        }
                        IComponent component=constructor.instance(dc);
                        //存入集合
                        ParasiticComponentRepositoryFactory.registerParasiticComponent(host,component,url);
                        //ExposedServiceInstanceStorageManager.registerExposedServiceInstance(proxyUrlEncrypt + ExposedServiceInstanceStorageManager.PATH_KEY_SEPARATOR + method.getName() + ExposedServiceInstanceStorageManager.PATH_KEY_SEPARATOR + host.hashCode(), dynamicComponent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    private static class ParasiticComponentFromHostConstructor implements IConstructor{
        private Object host;
        private IPCChat chat;
        protected ParasiticComponentFromHostConstructor(Object host,IPCChat chat){
            this.host=host;
            this.chat=chat;
        }
        @Override
        public <T> T instance(Class<T> cls) throws Exception {
            Constructor constructor = null;
            try {
                constructor = cls.getDeclaredConstructor(cls, IPCChat.class);
            } catch (Exception e) {
                e.printStackTrace();
                constructor = cls.getConstructor(cls, IPCChat.class);
            }
            constructor.setAccessible(true);
            IComponent dynamicComponent = (IComponent) constructor.newInstance(host, chat);
            return (T) dynamicComponent;
        }
    }

    public static <O> void unregisterParasiticComponentFromHost(O host) {
        Class<?> cls = host.getClass();
        String hostClsName = cls.getCanonicalName();
        Method[] methods = cls.getDeclaredMethods();

        for (Method method : methods
        ) {
            IPCChat chat = method.getAnnotation(IPCChat.class);
            if (chat != null) {

                String[] urls = chat.url();
                for (String url : urls) {
                    //String proxyUrlMD5 = EncryptUtil.getInstance().XORencode(url, "abc123");//ValidUtils.encryptMD5ToString(url);

                    try {
                        //Log.e("ipc","------------------->开始注销宿主"+hostClsName+"的事件接收器"+proxyUrlMD5);
                        //ExposedServiceInstanceStorageManager.unregisterExposedServiceInstance(proxyUrlMD5 + ExposedServiceInstanceStorageManager.PATH_KEY_SEPARATOR + method.getName() + ExposedServiceInstanceStorageManager.PATH_KEY_SEPARATOR + host.hashCode());
                        ParasiticComponentRepositoryFactory.unregisterParasiticComponent(host);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}

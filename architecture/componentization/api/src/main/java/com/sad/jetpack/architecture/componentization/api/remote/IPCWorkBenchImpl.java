package com.sad.jetpack.architecture.componentization.api.remote;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;

import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.architecture.componentization.api.IPerformer;
import com.sad.jetpack.architecture.componentization.api.IProceedListener;
import com.sad.jetpack.architecture.componentization.api.remote.handler.ProcessChatHandler;
import com.sad.jetpack.architecture.componentization.api.remote.handler.ProcessResponseHandler;
import static com.sad.jetpack.architecture.componentization.api.remote.IPCTransmission.*;

public class IPCWorkBenchImpl {


    private static ProcessResponseHandler processResponseHandler =new ProcessResponseHandler();
    public final static Messenger processSingleClientMessenger=new Messenger(processResponseHandler);



    public static boolean connect(Context context,String app,AppIPCServiceConnectionCallback connectionCallback){
        Intent intent;
        boolean isBindCurrAppMainProcess= TextUtils.isEmpty(app) || context.getPackageName().equals(app);
        if (isBindCurrAppMainProcess) {
            //Log.e("ipc","------------------->连接当前App服务端："+app);
            intent = new Intent(context,AppIPCService.class);
        }
        else {
            //跨App调起事件分发服务
            //Log.e("ipc","------------------->连接其他App服务端："+app);
            intent = new Intent(app + ".ipc");
            intent.setPackage(app);
        }
        return context.bindService(intent, connectionCallback, Context.BIND_AUTO_CREATE);
    }
    public static void registerCurrProcessClientMessenger(Context context){
        sendMessage(context,REGISTER_TO_MESSENGERS_POOL,"","",new Bundle(),null);
    }
    public static void unregisterCurrProcessClientMessenger(Context context){
        sendMessage(context,UNREGISTER_FROM_MESSENGERS_POOL,"","",new Bundle(),null);
    }

    public static void sendMessage(Context context,int target,String toApp,String toProcess,Bundle bundle, IProceedListener listener){
        sendMessage(context, target, toApp, toProcess, bundle, listener, new IPerformer() {
            @Override
            public void start(IDataCarrier data, boolean restart, long delay) {

            }
        });
    }

    public static void sendMessage(Context context, int target, String toApp, String toProcess, Bundle bundle, IProceedListener listener, IPerformer performer){


        boolean succ=connect(context, toApp, new AppIPCServiceConnectionCallback() {
            @Override
            public void onGetMessenger(ComponentName name, Messenger serverMessenger) {

                try {
                    Messenger replyMessenger=null;
                    if (target==REGISTER_TO_MESSENGERS_POOL){
                        replyMessenger=processSingleClientMessenger;
                    }
                    else if (target==CREATE_REMOTE_IPC_CHAT){
                        replyMessenger=new Messenger(new ProcessChatHandler(performer,listener));
                    }
                    IPCTransmissionImpl.with(context)
                            .bundle(bundle)
                            .classLoader(getClass().getClassLoader())
                            .listener(listener)
                            .messenger(serverMessenger)
                            .target(target)
                            .toApp(toApp)
                            .toProcess(toProcess)
                            .replyTo(replyMessenger)
                            .submit();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener!=null){
                        listener.onExceptionInPerformer(e);
                    }
                }
            }
        });
        if (!succ){
            if (listener!=null){
                listener.onExceptionInPerformer(new Exception("target "+toApp+":"+toProcess+" is not connected !!!"));
            }
        }
    }
}

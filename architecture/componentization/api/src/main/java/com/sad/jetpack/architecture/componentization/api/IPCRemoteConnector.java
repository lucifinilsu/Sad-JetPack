package com.sad.jetpack.architecture.componentization.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;

public class IPCRemoteConnector {
    private static ComponentResponseHandler processResponseHandler =new ComponentResponseHandler();
    protected final static Messenger processSingleClientMessenger=new Messenger(processResponseHandler);
    public static boolean connect(Context context, String app, AppIPCServiceConnectionCallback connectionCallback){
        Intent intent;
        boolean isBindCurrAppMainProcess= TextUtils.isEmpty(app) || context.getPackageName().equals(app);
        if (isBindCurrAppMainProcess) {
            //Log.e("ipc","------------------->连接当前App服务端："+app);
            intent = new Intent(context, AppIPCService.class);
        }
        else {
            //跨App调起事件分发服务
            //Log.e("ipc","------------------->连接其他App服务端："+app);
            intent = new Intent(app + ".ipc");
            intent.setPackage(app);
        }
        return context.bindService(intent, connectionCallback, Context.BIND_AUTO_CREATE);
    }

    public static boolean connect(Context context,Message message,IPCMessageTransmissionConfig config,IPCResultCallback callback) throws Exception{
        if (message==null){
            message=Message.obtain();
        }
        Message finalMessage = message;
        if (config==null){
            throw new Exception("ur IPCMessageTransmissionConfig is null !!!");
        }
        IPCTarget target=config.target();
        if (target==null){
            throw new Exception("ur IPCTarget is null !!!");
        }
        boolean success=connect(context, target.toApp(), new AppIPCServiceConnectionCallback() {
            @Override
            public void onGetMessenger(ComponentName name, Messenger serverMessenger) {
                try {

                    IPCLauncherImpl.newInstance(context)
                            .transmissionConfig(config)
                            .sendMessage(serverMessenger, finalMessage,callback)
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback!=null){
                        callback.onException(config,e);
                    }
                }
            }
        });
        return success;
    }

    public static boolean registerMessengerToServer(Context context) throws Exception{
        return registerMessengerToServer(context,processSingleClientMessenger);
    }
    public static boolean registerMessengerToServer(Context context,Messenger messenger) throws Exception{
        return connect(context,Message.obtain(),IPCMessageTransmissionConfigImpl.newInstance()
                .target(IPCTargetImpl.toProcess(context))
                .fromApp(context.getPackageName())
                .fromProcess(Utils.getCurrAppProccessName(context))
                .destType(CommonConstant.REGISTER_TO_MESSENGERS_POOL)
                ,null
        );
    }

    public static boolean sendMessage(Context context,Message message,IPCTarget target,IPCResultCallback callback) throws Exception{
        return connect(context,message,IPCMessageTransmissionConfigImpl.newInstance()
                .target(target)
                .fromApp(context.getPackageName())
                .fromProcess(Utils.getCurrAppProccessName(context))
                .destType(CommonConstant.CREATE_REMOTE_IPC_CHAT)
                ,callback
                );
    }
    public static boolean sendMessageToCurrProcess(Context context,Message message,String url,IPCResultCallback callback) throws Exception{
        return sendMessage(context,message,IPCTargetImpl.toProcess(context).url(url),callback);
    }

}

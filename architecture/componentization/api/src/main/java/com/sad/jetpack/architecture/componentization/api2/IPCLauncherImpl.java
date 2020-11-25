package com.sad.jetpack.architecture.componentization.api2;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;


public class IPCLauncherImpl implements IPCLauncher{
    private Context context;
    private IPCMessageTransmissionConfig config=IPCMessageTransmissionConfigImpl.newInstance();
    public static IPCLauncher newInstance(Context context){
        return new IPCLauncherImpl(context);
    }
    private IPCLauncherImpl(Context context){
        this.context=context;
    };

    @Override
    public IPCLauncher transmissionConfig(IPCMessageTransmissionConfig config) {
        this.config=config;
        return this;
    }

    @Override
    public IPCLauncher transmissionConfig(IPCTarget target, int destType) {
        IPCMessageTransmissionConfigImpl config=IPCMessageTransmissionConfigImpl.newInstance()
                .fromApp(context.getPackageName())
                .fromProcess(Utils.getCurrAppProccessName(context))
                .target(target)
                .destType(destType)
                ;
        this.config=config;
        return this;
    }

    @Override
    public IPCLauncher transmissionConfig(String url, int processorMode, int destType) {
        IPCTargetImpl targetImpl=IPCTargetImpl.toCurrProcess(context)
                .url(url)
                .processorMode(processorMode)
                ;
        return transmissionConfig(targetImpl,destType);
    }

    @Override
    public boolean sendMessage(Messenger messenger, Message message,IPCResultCallback callback) throws Exception {
        if (message==null){
            message=Message.obtain();
        }
        if (config==null){
            throw new Exception("ur remote config is null !!!");
        }
        Bundle bundle=message.getData();
        if (bundle==null){
            bundle=new Bundle();
        }
        bundle.putParcelable(CommonConstant.REMOTE_BUNDLE_TC,config);
        message.setData(bundle);
        Messenger replyMessengerProxy=new Messenger(new CallbackHandler(config,callback));
        message.replyTo=replyMessengerProxy;
        messenger.send(message);
        return false;
    }
    private static class CallbackHandler extends Handler {
        private WeakReference<IPCResultCallback> callbackWeakReference;
        private WeakReference<IPCMessageTransmissionConfig> ipcMessageTransmissionConfigWeakReference;
        protected CallbackHandler(IPCMessageTransmissionConfig transmissionConfig,IPCResultCallback callback){
            this.ipcMessageTransmissionConfigWeakReference=new WeakReference<IPCMessageTransmissionConfig>(transmissionConfig);
            this.callbackWeakReference=new WeakReference<IPCResultCallback>(callback);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            IPCMessageTransmissionConfig transmissionConfig=null;
            IPCResultCallback callback=null;
            if (ipcMessageTransmissionConfigWeakReference!=null){
                transmissionConfig=ipcMessageTransmissionConfigWeakReference.get();
            }
            if (callbackWeakReference!=null){
                callback=callbackWeakReference.get();
            }
            if (msg.obj!=null && msg.obj instanceof Throwable){
                if (callback!=null){
                    callback.onException(transmissionConfig,(Throwable) msg.obj);
                }
            }
            else {
                if (callback!=null){
                    callback.onDone(msg,transmissionConfig);
                }
            }
        }
    }
}

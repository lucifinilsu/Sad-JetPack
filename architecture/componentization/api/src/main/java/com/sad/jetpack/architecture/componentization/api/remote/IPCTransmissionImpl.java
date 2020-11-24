package com.sad.jetpack.architecture.componentization.api.remote;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.sad.jetpack.architecture.componentization.api.CommonConstant;
import com.sad.jetpack.architecture.componentization.api.IProceedListener;
import com.sad.jetpack.architecture.componentization.api.utils.Utils;
@Deprecated
public class IPCTransmissionImpl implements IPCTransmission {

    private String toApp="";
    private String toProcess="";
    private Messenger messenger;
    private Messenger replyToMessenger;
    private int target=REGISTER_TO_MESSENGERS_POOL;
    private Bundle bundle=new Bundle();
    private IProceedListener listener;
    private Context context;
    private ClassLoader classLoader;
    public static IPCTransmission with(Context context){
        return new IPCTransmissionImpl(context);
    }

    private IPCTransmissionImpl(Context context){
        this.context=context;
    }

    @Override
    public IPCTransmission classLoader(ClassLoader classLoader) {
        this.classLoader=classLoader;
        return this;
    }

    @Override
    public IPCTransmission toApp(String app) {
        this.toApp=app;
        return this;
    }

    @Override
    public IPCTransmission toProcess(String process) {
        this.toProcess=process;
        return this;
    }

    @Override
    public IPCTransmission messenger(Messenger messenger) {
        this.messenger=messenger;
        return this;
    }

    @Override
    public IPCTransmission replyTo(Messenger replyToMessenger) {
        this.replyToMessenger=replyToMessenger;
        return this;
    }

    @Override
    public IPCTransmission target(int target) {
        this.target=target;
        return this;
    }

    @Override
    public IPCTransmission bundle(Bundle bundle) {
        this.bundle=bundle;
        return this;
    }

    @Override
    public IPCTransmission listener(IProceedListener listener) {
        this.listener=listener;
        return this;
    }

    @Override
    public void submit() {
        try {
            bundle.putString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_FROM_APP,context.getPackageName());
            bundle.putString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_FROM_PROCESS, Utils.getCurrAppProccessName(context));
            bundle.putString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_TO_APP,toApp);
            bundle.putString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_TO_PROCESS,toProcess);
            Message message=Message.obtain();
            if (classLoader==null){
                classLoader=getClass().getClassLoader();
            }
            bundle.setClassLoader(classLoader);
            message.setData(bundle);
            message.replyTo=replyToMessenger;
            message.what=target;
            message.arg1=-1;
            message.arg2=-1;
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            if (listener!=null){
                listener.onExceptionInPerformer(e);
            }
        }
    }

}

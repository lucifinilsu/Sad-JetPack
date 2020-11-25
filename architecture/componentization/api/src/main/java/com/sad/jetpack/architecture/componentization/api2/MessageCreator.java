package com.sad.jetpack.architecture.componentization.api2;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;

public class MessageCreator {

    public static Message createThrowableMessage(
            Message message,
            String app,
            String process,
            String url,
            int destType,
            Throwable throwable
    ){
        IPCMessageTransmissionConfig transmissionConfig=IPCMessageTransmissionConfigImpl.fromCurrProcess(InternalContextHolder.get().getContext())
                .destType(destType)
                .target(IPCTargetImpl.newInstance()
                    .url(url)
                    .toApp(app)
                    .toProcess(process)
                );
        if (message==null){
            message=Message.obtain();
        }
        message.obj=throwable;
        Bundle bundle=new Bundle();
        bundle.putParcelable(CommonConstant.REMOTE_BUNDLE_TC,transmissionConfig);
        message.setData(bundle);
        return message;
    }
    public static Message createThrowableMessage(
            Message message,
            IPCMessageTransmissionConfig transmissionConfig,
            Throwable throwable
    ){
        if (message==null){
            message=Message.obtain();
        }
        message.obj=throwable;
        Bundle bundle=new Bundle();
        bundle.putParcelable(CommonConstant.REMOTE_BUNDLE_TC,transmissionConfig);
        message.setData(bundle);
        return message;
    }

    public static IPCMessageTransmissionConfig standardMessage(Message message,String url,long delay,long timeout,int processorMode){
        return standardMessage(InternalContextHolder.get().getContext(),message,url,delay,timeout,processorMode);
    }


    public static IPCMessageTransmissionConfig standardMessage(Context context,Message message, String url, long delay, long timeout, int processorMode){
        Bundle bundle=message.getData();
        IPCMessageTransmissionConfig transmissionConfig=IPCMessageTransmissionConfigImpl.fromCurrProcess(context)
                .destType(CommonConstant.CREATE_REMOTE_IPC_CHAT)
                .target(IPCTargetImpl.toCurrProcess(InternalContextHolder.get().getContext())
                        .url(url)
                        .delay(delay)
                        .timeout(timeout)
                        .processorMode(processorMode)
                );
        Parcelable p=bundle.getParcelable(CommonConstant.REMOTE_BUNDLE_TC);
        if (bundle!=null && p!=null){
            transmissionConfig= (IPCMessageTransmissionConfig) p;
        }
        else {
            bundle.putParcelable(CommonConstant.REMOTE_BUNDLE_TC,transmissionConfig);
            message.setData(bundle);
        }
        return transmissionConfig;
    }

}

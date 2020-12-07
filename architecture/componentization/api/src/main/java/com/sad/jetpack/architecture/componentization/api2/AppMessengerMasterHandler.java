package com.sad.jetpack.architecture.componentization.api2;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.annotation.NonNull;


import java.util.concurrent.ConcurrentHashMap;

import static com.sad.jetpack.architecture.componentization.api2.CommonConstant.*;
import static com.sad.jetpack.architecture.componentization.api2.RemoteAction.*;
import static com.sad.jetpack.architecture.componentization.api2.RemoteActionResultState.*;

/**
 * 管理所有链接到本App的信使，及信使信息的转发
 */
final class AppMessengerMasterHandler extends Handler {

    private final static ConcurrentHashMap<String,ConcurrentHashMap<String, Messenger>> MESSENGERS_POOL =new ConcurrentHashMap<>();

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        Bundle bundle=msg.getData();
        if (bundle!=null){
            int action=bundle.getInt(CommonConstant.REMOTE_BUNDLE_ACTION);
            IRequest request=bundle.getParcelable(REMOTE_BUNDLE_REQUEST);
            ITarget target=bundle.getParcelable(REMOTE_BUNDLE_TARGET);
            String fromApp=request.fromApp();
            String fromProcess=request.fromProcess();
            String toApp=target.toApp();
            String toProcess=target.toProcess();
            try {
                if (action== REMOTE_ACTION_REGISTER_TO_MESSENGERS_POOL){
                    Messenger processClientMessenger=msg.replyTo;
                    if (processClientMessenger==null){
                        throw new Exception("The messenger("+fromApp+":"+fromProcess+") you wanna register to MESSENGERS_POOL is null,please check out 'msg.replyTo'.");
                    }
                    registerToMessengersPool(processClientMessenger,fromApp,fromProcess);
                }
                else if (action== REMOTE_ACTION_CREATE_REMOTE_IPC_CHAT){
                    Messenger messenger=findMessenger(toApp,toProcess);
                    if (null==messenger){
                        throw new Exception("The messenger("+fromApp+":"+fromProcess+") that you wanna use to send some messages is null or not found,please check out the messenger.");
                    }
                    else {
                        messenger.send(msg);
                    }
                }
                else if (action == REMOTE_ACTION_UNREGISTER_FROM_MESSENGERS_POOL){
                    Messenger processClientMessenger=msg.replyTo;
                    if (processClientMessenger==null){
                        throw new Exception("The messenger("+fromApp+":"+fromProcess+") you wanna unregister to MESSENGERS_POOL is null,please check out 'msg.replyTo'.");
                    }
                    unregisterToMessengersPool(processClientMessenger,fromApp,fromProcess);
                }
            }catch (Exception e){
                e.printStackTrace();
                Messenger callbackMessenger=msg.replyTo;
                msg.what=REMOTE_ACTION_RESULT_STATE_FAILURE;
                bundle.setClassLoader(getClass().getClassLoader());
                bundle.putSerializable(REMOTE_BUNDLE_THROWABLE,e);
                if (callbackMessenger!=null){
                    try {
                        callbackMessenger.send(msg);
                    } catch (RemoteException remoteException) {
                        remoteException.printStackTrace();
                    }
                }
            }
        }
    }

    private void registerToMessengersPool(Messenger messenger,String fromApp,String fromProcess){
        ConcurrentHashMap<String, Messenger> processMessengers= MESSENGERS_POOL.get(fromApp);
        if (processMessengers==null){
            processMessengers=new ConcurrentHashMap<>();
        }
        processMessengers.put(fromProcess,messenger);
        MESSENGERS_POOL.put(fromApp,processMessengers);
    }

    private void unregisterToMessengersPool(Messenger messenger,String fromApp,String fromProcess){
        ConcurrentHashMap<String, Messenger> processMessengers= MESSENGERS_POOL.get(fromApp);
        if (processMessengers!=null){
            if (processMessengers.contains(fromProcess)){
                processMessengers.remove(fromProcess);
                MESSENGERS_POOL.put(fromApp,processMessengers);
            }
        }

    }

    private Messenger findMessenger(String toApp,String toProcess){
        ConcurrentHashMap<String, Messenger> processMessengers= MESSENGERS_POOL.get(toApp);
        if (processMessengers==null){
            processMessengers=new ConcurrentHashMap<>();
        }
        Messenger messenger=processMessengers.get(toProcess);
        return messenger;
    }
}

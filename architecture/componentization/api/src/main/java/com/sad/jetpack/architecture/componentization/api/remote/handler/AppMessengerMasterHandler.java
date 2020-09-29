package com.sad.jetpack.architecture.componentization.api.remote.handler;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

import androidx.annotation.NonNull;

import com.sad.jetpack.architecture.componentization.api.CommonConstant;

import java.util.concurrent.ConcurrentHashMap;

import static com.sad.jetpack.architecture.componentization.api.remote.IPCTransmission.*;

/**
 * 管理所有链接到本App的信使，及信使信息的转发
 */
public class AppMessengerMasterHandler extends Handler {

    private final static ConcurrentHashMap<String,ConcurrentHashMap<String, Messenger>> MESSEENGERS_POOL=new ConcurrentHashMap<>();

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        Bundle bundle=msg.getData();
        String toApp=bundle.getString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_TO_APP);
        String toProcess=bundle.getString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_TO_PROCESS);
        String fromApp=bundle.getString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_FROM_APP);
        String fromProcess=bundle.getString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_FROM_PROCESS);
        int target=msg.what;
        try {

            if (target== REGISTER_TO_MESSENGERS_POOL){
                Messenger processClientMessenger=msg.replyTo;
                if (processClientMessenger==null){
                    throw new Exception("The messenger("+fromApp+":"+fromProcess+") you wanna register to MESSENGERS_POOL is null,please check out 'msg.replyTo'.");
                }
                registerToMessengersPool(processClientMessenger,fromApp,fromProcess);
            }
            else if (target== CREATE_REMOTE_IPC_CHAT){
                Messenger messenger=findMessenger(toApp,toProcess);
                if (null==messenger){
                    Messenger callbackMessenger=msg.replyTo;
                    msg.arg1=CommonConstant.REMOTE_CHAT_ES_CALLBACK_EXCEPTION;
                    msg.setData(bundle);
                    callbackMessenger.send(msg);
                }
                else {
                    messenger.send(msg);
                }
            }
            else if (target == UNREGISTER_FROM_MESSENGERS_POOL){
                Messenger processClientMessenger=msg.replyTo;
                if (processClientMessenger==null){
                    throw new Exception("The messenger("+fromApp+":"+fromProcess+") you wanna unregister to MESSENGERS_POOL is null,please check out 'msg.replyTo'.");
                }
                unregisterToMessengersPool(processClientMessenger,fromApp,fromProcess);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void registerToMessengersPool(Messenger messenger,String fromApp,String fromProcess){
        ConcurrentHashMap<String, Messenger> processMessengers=MESSEENGERS_POOL.get(fromApp);
        if (processMessengers==null){
            processMessengers=new ConcurrentHashMap<>();
        }
        processMessengers.put(fromProcess,messenger);
        MESSEENGERS_POOL.put(fromApp,processMessengers);
    }

    private void unregisterToMessengersPool(Messenger messenger,String fromApp,String fromProcess){
        ConcurrentHashMap<String, Messenger> processMessengers=MESSEENGERS_POOL.get(fromApp);
        if (processMessengers!=null){
            if (processMessengers.contains(fromProcess)){
                processMessengers.remove(fromProcess);
                MESSEENGERS_POOL.put(fromApp,processMessengers);
            }
        }

    }

    private Messenger findMessenger(String toApp,String toProcess){
        ConcurrentHashMap<String, Messenger> processMessengers=MESSEENGERS_POOL.get(toApp);
        if (processMessengers==null){
            processMessengers=new ConcurrentHashMap<>();
        }
        Messenger messenger=processMessengers.get(toProcess);
        return messenger;
    }
}

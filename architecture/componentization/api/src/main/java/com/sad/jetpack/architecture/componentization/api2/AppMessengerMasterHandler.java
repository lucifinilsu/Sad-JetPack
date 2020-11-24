package com.sad.jetpack.architecture.componentization.api2;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

import androidx.annotation.NonNull;


import java.util.concurrent.ConcurrentHashMap;

import static com.sad.jetpack.architecture.componentization.api.remote.IPCTransmission.CREATE_REMOTE_IPC_CHAT;
import static com.sad.jetpack.architecture.componentization.api.remote.IPCTransmission.REGISTER_TO_MESSENGERS_POOL;
import static com.sad.jetpack.architecture.componentization.api.remote.IPCTransmission.UNREGISTER_FROM_MESSENGERS_POOL;

/**
 * 管理所有链接到本App的信使，及信使信息的转发
 */
public class AppMessengerMasterHandler extends Handler {

    private final static ConcurrentHashMap<String,ConcurrentHashMap<String, Messenger>> MESSEENGERS_POOL=new ConcurrentHashMap<>();

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        Bundle bundle=msg.getData();
        if (bundle!=null){
            IPCMessageTransmissionConfig tc=bundle.getParcelable(CommonConstant.REMOTE_BUNDLE_TC);
            int destType=tc.destType();
            String fromApp=tc.fromApp();
            String fromProcess=tc.fromProcess();
            String toApp=tc.target().toApp();
            String toProcess=tc.target().toProcess();
            try {

                if (destType== REGISTER_TO_MESSENGERS_POOL){
                    Messenger processClientMessenger=msg.replyTo;
                    if (processClientMessenger==null){
                        throw new Exception("The messenger("+fromApp+":"+fromProcess+") you wanna register to MESSENGERS_POOL is null,please check out 'msg.replyTo'.");
                    }
                    registerToMessengersPool(processClientMessenger,fromApp,fromProcess);
                }
                else if (destType== CREATE_REMOTE_IPC_CHAT){
                    Messenger messenger=findMessenger(toApp,toProcess);
                    if (null==messenger){
                        throw new Exception("The messenger("+fromApp+":"+fromProcess+") that you wanna use to send some messages is null or not found,please check out the messenger.");
                    }
                    else {
                        messenger.send(msg);
                    }
                }
                else if (destType == UNREGISTER_FROM_MESSENGERS_POOL){
                    Messenger processClientMessenger=msg.replyTo;
                    if (processClientMessenger==null){
                        throw new Exception("The messenger("+fromApp+":"+fromProcess+") you wanna unregister to MESSENGERS_POOL is null,please check out 'msg.replyTo'.");
                    }
                    unregisterToMessengersPool(processClientMessenger,fromApp,fromProcess);
                }
            }catch (Exception e){
                e.printStackTrace();
                Messenger callbackMessenger=msg.replyTo;
                msg.obj=e;
                try {
                    callbackMessenger.send(msg);
                }
                catch (Exception e2){
                    e2.printStackTrace();
                }

            }
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

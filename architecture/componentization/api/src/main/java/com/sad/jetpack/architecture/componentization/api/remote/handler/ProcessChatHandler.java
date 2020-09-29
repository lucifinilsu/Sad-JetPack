package com.sad.jetpack.architecture.componentization.api.remote.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.sad.jetpack.architecture.componentization.api.CommonConstant;
import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.architecture.componentization.api.IPCMessenger;
import com.sad.jetpack.architecture.componentization.api.IPCSession;
import com.sad.jetpack.architecture.componentization.api.IPerformer;
import com.sad.jetpack.architecture.componentization.api.IProceedListener;

import java.io.Serializable;

public class ProcessChatHandler extends Handler{
    private IProceedListener proceedListener;
    private IPerformer performer;
    public ProcessChatHandler(IPerformer performer,IProceedListener proceedListener) {
        this.proceedListener = proceedListener;
        this.performer=performer;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        Bundle bundle=msg.getData();
        int c=msg.arg1;
        if (c==CommonConstant.REMOTE_CHAT_ES_CALLBACK_EXCEPTION){
            Serializable se =bundle.getSerializable(CommonConstant.REMOTE_BUNDLE_PARAMETERS_EXCEPTION);
            if (se!=null){
                Exception exception= (Exception) se;
                if (proceedListener!=null){
                    proceedListener.onExceptionInPerformer(exception);
                }
            }
            else {
                if (proceedListener!=null){
                    proceedListener.onExceptionInPerformer(new Exception("remote task error,but has not received exception object as 'REMOTE_CHAT_ES_CALLBACK_EXCEPTION' !!!"));
                }
            }
        }
        else if (c==CommonConstant.REMOTE_CHAT_ES_CALLBACK_PROCEED){
            IDataCarrier dataCarrier=bundle.getParcelable(CommonConstant.REMOTE_BUNDLE_PARAMETERS_DATACARRIER);
            String messageProxyId=bundle.getString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_MESSAGEPROXY_ID);
            if (proceedListener!=null){
                proceedListener.onProceed(dataCarrier, new IPCSession(){
                    @Override
                    public String sessionId() {
                        return messageProxyId;
                    }

                    @Override
                    public boolean componentChat(IDataCarrier o, IPCMessenger messenger) {
                        bundle.putParcelable(CommonConstant.REMOTE_BUNDLE_PARAMETERS_DATACARRIER,o);
                        return false;
                    }
                },messageProxyId);
            }
        }
        else if (c==CommonConstant.REMOTE_CHAT_ES_CALLBACK_OUTPUT){
            if (proceedListener!=null){
                IDataCarrier dataCarrier=bundle.getParcelable(CommonConstant.REMOTE_BUNDLE_PARAMETERS_DATACARRIER);
                proceedListener.onOutput(dataCarrier);
            }
        }
        else if (c==CommonConstant.REMOTE_CHAT_ES_CALLBACK_INTERCEPTED){
            IDataCarrier dataCarrier=bundle.getParcelable(CommonConstant.REMOTE_BUNDLE_PARAMETERS_DATACARRIER);
            proceedListener.onIntercepted(this.performer,null,dataCarrier);
        }

    }

}

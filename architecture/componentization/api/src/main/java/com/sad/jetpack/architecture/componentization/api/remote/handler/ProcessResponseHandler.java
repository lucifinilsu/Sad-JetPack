package com.sad.jetpack.architecture.componentization.api.remote.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.sad.jetpack.architecture.componentization.api.CommonConstant;
import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IPCSession;
import com.sad.jetpack.architecture.componentization.api.IPerformer;
import com.sad.jetpack.architecture.componentization.api.IProceedListener;
import com.sad.jetpack.architecture.componentization.api.IComponentProcessor;
import com.sad.jetpack.architecture.componentization.api.SCore;
import com.sad.jetpack.architecture.componentization.api.impl.DataCarrierImpl;
import com.sad.jetpack.architecture.componentization.api.remote.IPCWorkBenchImpl;

import java.util.HashMap;
import java.util.Map;

public class ProcessResponseHandler extends Handler {
    private Map<String,IPerformer> performers=new HashMap<>();
    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        Messenger callback=msg.replyTo;
        Bundle bundle = msg.getData();
        IDataCarrier dataCarrier= DataCarrierImpl.newInstanceCreator().data(bundle).create();//bundle.getParcelable(CommonConstant.REMOTE_BUNDLE_PARAMETERS_DATACARRIER);
        long timeout=bundle.getLong(CommonConstant.REMOTE_BUNDLE_PARAMETERS_TIMEOUT,3000);
        String orgUrl=bundle.getString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_ORGURL);
        int proceedMode=bundle.getInt(CommonConstant.REMOTE_BUNDLE_PARAMETERS_PROCEED_MODE, IComponentProcessor.PROCEED_MODE_SEQUENCE);
        bundle.putString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_FROM_APP,bundle.getString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_TO_APP));
        bundle.putString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_FROM_PROCESS, bundle.getString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_TO_PROCESS));
        bundle.putString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_TO_APP,bundle.getString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_FROM_APP));
        bundle.putString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_TO_PROCESS,bundle.getString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_FROM_PROCESS));
        if (TextUtils.isEmpty(orgUrl)){
            msg.arg1=CommonConstant.REMOTE_CHAT_ES_CALLBACK_EXCEPTION;
            bundle.putSerializable(CommonConstant.REMOTE_BUNDLE_PARAMETERS_EXCEPTION,new Exception("ur remote orgUrl is null !!!"));
            msg.setData(bundle);
            try {
                callback.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        else {
            boolean isContinueFromIntercepted=bundle.getBoolean(CommonConstant.REMOTE_BUNDLE_PARAMETERS_PROCEED_FROM_INTERCEPTED,false);
            if (isContinueFromIntercepted && performers.containsKey(orgUrl)){
                IPerformer performer=performers.get(orgUrl);
                performer.start(dataCarrier);
            }
            else {
                IPerformer performer=SCore.getManager()
                        .cluster(orgUrl)
                        .post()
                        .proceedMode(proceedMode)
                        .timeout(timeout)
                        .listener(new IProceedListener() {
                            @Override
                            public IDataCarrier onInput(IDataCarrier inputData) {
                                return inputData;
                            }

                            @Override
                            public boolean onProceed(IDataCarrier data, IPCSession session, String messengerProxyId) {
                                bundle.putParcelable(CommonConstant.REMOTE_BUNDLE_PARAMETERS_DATACARRIER,data);
                                bundle.putString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_MESSAGEPROXY_ID,messengerProxyId);
                                msg.replyTo= IPCWorkBenchImpl.processSingleClientMessenger;
                                msg.setData(bundle);
                                msg.arg1=CommonConstant.REMOTE_CHAT_ES_CALLBACK_PROCEED;
                                try {
                                    callback.send(msg);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }

                            @Override
                            public void onOutput(ConcurrentLinkedHashMap<String, IDataCarrier> outPutData) {
                                bundle.putSerializable(CommonConstant.REMOTE_BUNDLE_PARAMETERS_DATACARRIER,outPutData);
                                msg.setData(bundle);
                                msg.arg1=CommonConstant.REMOTE_CHAT_ES_CALLBACK_OUTPUT_MAP;
                                try {
                                    callback.send(msg);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onOutput(IDataCarrier totalOutputData) {
                                bundle.putParcelable(CommonConstant.REMOTE_BUNDLE_PARAMETERS_DATACARRIER,totalOutputData);
                                msg.setData(bundle);
                                msg.arg1=CommonConstant.REMOTE_CHAT_ES_CALLBACK_OUTPUT;
                                try {
                                    callback.send(msg);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onExceptionInPerformer(Throwable throwable) {
                                msg.arg1=CommonConstant.REMOTE_CHAT_ES_CALLBACK_EXCEPTION;
                                bundle.putSerializable(CommonConstant.REMOTE_BUNDLE_PARAMETERS_EXCEPTION,throwable);
                                msg.setData(bundle);
                                try {
                                    callback.send(msg);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onIntercepted(IPerformer performer, IExposedService lastExposedService, IDataCarrier outputData) {
                                performers.put(orgUrl,performer);
                                bundle.putParcelable(CommonConstant.REMOTE_BUNDLE_PARAMETERS_DATACARRIER,outputData);
                                msg.setData(bundle);
                                msg.arg1=CommonConstant.REMOTE_CHAT_ES_CALLBACK_INTERCEPTED;
                                try {
                                    callback.send(msg);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .submit();
                performer.start(dataCarrier);
            }
            ;

        }
    }
}

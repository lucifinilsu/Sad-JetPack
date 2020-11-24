package com.sad.jetpack.architecture.componentization.api2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

public class ComponentResponseHandler extends Handler{
    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        Bundle bundle=msg.getData();
        Messenger replyMessenger=msg.replyTo;
        if (bundle!=null){
            IPCMessageTransmissionConfig tc=bundle.getParcelable(CommonConstant.REMOTE_BUNDLE_TC);
            IPCTarget target=tc.target();
            String url=target.url();
            if (!TextUtils.isEmpty(url)){
                IComponentsCluster cluster=SCore.getCluster();
                int pm=tc.target().prcessorMode();
                IComponentProcessorBuilder processorBuilder=ComponentProcessorBuilderImpl.newBuilder(url)
                        .timeout(target.timeout())
                        .delay(target.delay())
                        ;
                IPCComponentProcessorSession processorSession=new IPCComponentProcessorSession() {
                    @Override
                    public void onException(IPCMessageTransmissionConfig transmissionConfig, Throwable throwable) {
                        try {
                            if (replyMessenger!=null){
                                replyMessenger.send(MessageCreator.createThrowableMessage(msg,transmissionConfig,throwable));
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onProcessorOutput(String processorId, Message message) {

                    }

                    @Override
                    public void onProcessorOutput(ConcurrentLinkedHashMap<Message, String> messages) {

                    }

                    @Override
                    public void onComponentChat(String curl, Message message) {

                    }


                };
                if (pm==IPCTarget.PROCESSOR_MODE_SEQUENCE){
                    processorBuilder.asSequence()
                            .processorSession(processorSession)
                            .then(cluster.repository(url))
                            .submit(msg);
                }
                else if (pm==IPCTarget.PROCESSOR_MODE_CONCURRENCY){
                    processorBuilder.asConcurrency()
                            .processorSession(processorSession)
                            .parallel(cluster.repository(url))
                            .submit(msg);
                }
            }
        }
    }
}

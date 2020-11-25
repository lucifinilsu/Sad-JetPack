package com.sad.jetpack.architecture.componentization;

import android.content.Context;
import android.os.Message;
import android.os.Messenger;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.sad.jetpack.architecture.componentization.api2.ComponentProcessorBuilderImpl;
import com.sad.jetpack.architecture.componentization.api2.IComponent;
import com.sad.jetpack.architecture.componentization.api2.IPCComponentProcessorCombinerSession;
import com.sad.jetpack.architecture.componentization.api2.IPCComponentProcessorSession;
import com.sad.jetpack.architecture.componentization.api2.IPCMessageSender;
import com.sad.jetpack.architecture.componentization.api2.IPCMessageTransmissionConfig;
import com.sad.jetpack.architecture.componentization.api2.IPCResultCallback;
import com.sad.jetpack.architecture.componentization.api2.IPCTarget;
import com.sad.jetpack.architecture.componentization.api2.IPCTargetImpl;
import com.sad.jetpack.architecture.componentization.api2.SCore;
import com.sad.jetpack.architecture.componentization.api2.SimpleConstructorImpl;

public class DemoUser {
    public static void main(String[] args) {

        SCore.getComponentCallable("").call(Message.obtain(),new IPCResultCallback(){

            @Override
            public void onException(IPCMessageTransmissionConfig transmissionConfig, Throwable throwable) {

            }

            @Override
            public void onDone(Message msg, IPCMessageTransmissionConfig transmissionConfig) {

            }
        });
    }

    public static void testIPC(Context context){

        try {
            //1、初始化
            SCore.initIPC(context);
            //2、新建Message
            Message message=Message.obtain();
            //4、确定发送目标
            String url="hhh://iii.ooo.com";
            IPCTarget target= IPCTargetImpl.newInstance()
                    .toApp("com.xxx.uuu")
                    .toProcess("com.xxx.uuu:aaa")
                    .url(url)
                    ;
            //这时可以直接执行ipc方法发送message。
            SCore.ipc(context, message, target, new IPCResultCallback() {
                @Override
                public void onDone(Message msg, IPCMessageTransmissionConfig transmissionConfig) {

                }

                @Override
                public void onException(IPCMessageTransmissionConfig config, Throwable throwable) {

                }
            });
            //5、如果ipc任务是在队列中的，可以建个组件封装ipc任务
            IComponent component=new IComponent() {
                @Override
                public <T> T onCall(Message message, IPCMessageSender messageSender) {
                    try {
                        SCore.ipc(context, message, target, new IPCResultCallback() {
                            @Override
                            public void onDone(Message msg, IPCMessageTransmissionConfig transmissionConfig) {
                                Messenger reply=message.replyTo;
                                try {
                                    messageSender.sendMessage(reply,msg,null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onException(IPCMessageTransmissionConfig config,Throwable throwable) {

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                public String instanceOrgUrl() {
                    return url;
                }
            };
            //6、将组件置入任务队列，最后提交，这样就可以将ipc任务作为任务链其中一环进行执行了。
            ComponentProcessorBuilderImpl.newBuilder("ggg")
                    .delay(500)
                    .timeout(10000)
                    .asSequence()
                    .join(SCore.getCluster(context).repository("cscsc"))
                    .join(component,url)
                    .processorSession(new IPCComponentProcessorSession() {
                        @Override
                        public void onProcessorOutput(String processorId, Message message) {

                        }

                        @Override
                        public void onProcessorOutput(ConcurrentLinkedHashMap<Message, String> messages) {

                        }

                        @Override
                        public void onComponentChat(String curl, Message message) {

                        }

                        @Override
                        public void onException(IPCMessageTransmissionConfig config,Throwable throwable) {

                        }
                    })
                    .submit(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

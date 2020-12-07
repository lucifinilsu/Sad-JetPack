package com.sad.jetpack.architecture.componentization;

import android.content.Context;
import android.os.Message;

import com.sad.jetpack.architecture.componentization.api2.CallerConfigImpl;
import com.sad.jetpack.architecture.componentization.api2.IDataContainer;
import com.sad.jetpack.architecture.componentization.api2.IPCRemoteCallListener;
import com.sad.jetpack.architecture.componentization.api2.IPCRemoteConnectorImpl;
import com.sad.jetpack.architecture.componentization.api2.IRequestSession;
import com.sad.jetpack.architecture.componentization.api2.IResponse;
import com.sad.jetpack.architecture.componentization.api2.ITarget;
import com.sad.jetpack.architecture.componentization.api2.RemoteAction;
import com.sad.jetpack.architecture.componentization.api2.RequestImpl;
import com.sad.jetpack.architecture.componentization.api2.IRequest;
import com.sad.jetpack.architecture.componentization.api2.SCore;
import com.sad.jetpack.architecture.componentization.api2.TargetImpl;

public class DemoUser {
    public static void main(String[] args) {
        IRequest request= RequestImpl.newBuilder("cs")
                .addData("s",00)
                .addData("q",true)
                .build();

    }
    static void testIPC(IRequest request) throws Exception {
        Context context=null;
        SCore.ipc(context)
                .callerConfig(CallerConfigImpl.newBuilder().build())
                .action(RemoteAction.REMOTE_ACTION_CREATE_REMOTE_IPC_CHAT)
                .request(request)
                .target(TargetImpl.newBuilder().toApp("xxx.xxx").toProcess("xxx.xxx:aaa").id("123").build())
                .listener(new IPCRemoteCallListener() {
                    @Override
                    public boolean onRemoteCallReceivedResponse(IResponse response, IRequestSession session, ITarget target) {
                        IDataContainer dataContainer=response.dataContainer();
                        dataContainer.getMap().put("new request","干的很好");
                        session.replyRequestData(dataContainer);
                        return false;
                    }

                    @Override
                    public void onRemoteCallException(IRequest request, Throwable throwable, ITarget target) {

                    }
                })
                .build()
                .execute();
    }
    static void testITarget(){
        TargetImpl.newBuilder().id("").build();
    }
    /*static void testcall(){
        SCore.getComponentCallable("").call(Message.obtain(),new IPCResultCallback(){

            @Override
            public void onException(IPCMessageTransmissionConfig transmissionConfig, Throwable throwable) {

            }

            @Override
            public void onDone(Message msg, IPCMessageTransmissionConfig transmissionConfig) {

            }
        });
    }*/

    /*public static void testIPC(Context context){

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
                        public void onProcessorGenerate(ConcurrentLinkedHashMap<Message, String> messages) {

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
    }*/

}

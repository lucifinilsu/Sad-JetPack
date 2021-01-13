package com.sad.jetpack.architecture.componentization.api;

import android.content.Context;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.sad.jetpack.architecture.componentization.api.CallerConfigImpl;
import com.sad.jetpack.architecture.componentization.api.IComponentCallListener;
import com.sad.jetpack.architecture.componentization.api.IComponentProcessorCallListener;
import com.sad.jetpack.architecture.componentization.api.IDataContainer;
import com.sad.jetpack.architecture.componentization.api.IPCRemoteCallListener;
import com.sad.jetpack.architecture.componentization.api.IRequestSession;
import com.sad.jetpack.architecture.componentization.api.IResponse;
import com.sad.jetpack.architecture.componentization.api.ITarget;
import com.sad.jetpack.architecture.componentization.api.InternalContextHolder;
import com.sad.jetpack.architecture.componentization.api.ParasiticComponentRepositoryFactory;
import com.sad.jetpack.architecture.componentization.api.RemoteAction;
import com.sad.jetpack.architecture.componentization.api.RequestImpl;
import com.sad.jetpack.architecture.componentization.api.IRequest;
import com.sad.jetpack.architecture.componentization.api.SCore;
import com.sad.jetpack.architecture.componentization.api.TargetImpl;
import com.sad.jetpack.architecture.componentization.api.extension.router.ActivityRouterParameters;
import com.sad.jetpack.architecture.componentization.api.extension.router.IActivityRouterParameters;

public class DemoUser {
    public static void main(String[] args) {
        /*IRequest request= RequestImpl.newBuilder("cs")
                .addData("s",00)
                .addData("q",true)
                .build();
        ActivityRouterParameters.newInstance().toBuilder()
            .action("")
            .bundle(null)
            .resultLauncher(null)
            .build()
            ;
*/
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
                        IDataContainer dataContainer=response.body().dataContainer();
                        dataContainer.getMap().put("new request","干的很好");
                        session.replyRequestData(BodyImpl.newBuilder().dataContainer(dataContainer).build());
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

    static void testCall(IRequest request){
        Context context=null;
        SCore.asSequenceProcessor("sss")
                .listener(new IComponentProcessorCallListener() {
                    @Override
                    public boolean onProcessorReceivedResponse(IResponse response, String processorId) {
                        return false;
                    }

                    @Override
                    public IResponse onProcessorMergeResponses(ConcurrentLinkedHashMap<IResponse, String> responses, String processorId) {
                        return null;
                    }

                    @Override
                    public void onProcessorException(IRequest request, Throwable throwable, String processorId) {

                    }

                    @Override
                    public boolean onChildComponentReceivedResponse(IResponse response, IRequestSession session, String componentId) {
                        return false;
                    }

                    @Override
                    public void onChildComponentException(IRequest request, Throwable throwable, String componentId) {

                    }

                    @Override
                    public boolean onChildProcessorReceivedResponse(IResponse response, String childProcessorId) {
                        return false;
                    }

                    @Override
                    public void onChildProcessorException(IRequest request, Throwable throwable, String childProcessorId) {

                    }
                })
                .build()
                .submit(request);
        SCore.getCluster(InternalContextHolder.get().getContext())
                .instancesRepositoryFactory(ParasiticComponentRepositoryFactory.newInstance())
                .repository("xcccc")
                .firstComponentCallableInstance()
                .toBuilder()
                .listener(new IComponentCallListener() {
                    @Override
                    public boolean onComponentReceivedResponse(IResponse response, IRequestSession session, String componentId) {
                        return false;
                    }

                    @Override
                    public void onComponentException(IRequest request, Throwable throwable, String componentId) {

                    }
                })
                .build()
                .call(RequestImpl.newInstance("666"));

        SCore.getComponentCallable(InternalContextHolder.get().getContext(),"cxcxc")
                .toBuilder()
                .listener(new IComponentCallListener() {
                    @Override
                    public boolean onComponentReceivedResponse(IResponse response, IRequestSession session, String componentId) {
                        return false;
                    }

                    @Override
                    public void onComponentException(IRequest request, Throwable throwable, String componentId) {

                    }
                })
                .build()
                .call(RequestImpl.newInstance("666"));
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

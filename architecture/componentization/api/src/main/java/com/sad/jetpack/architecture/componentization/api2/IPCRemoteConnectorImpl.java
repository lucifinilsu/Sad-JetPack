package com.sad.jetpack.architecture.componentization.api2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class IPCRemoteConnectorImpl implements IPCRemoteConnector,IPCRemoteConnector.Builder{

    private final static ComponentResponseHandler processResponseHandler =new ComponentResponseHandler();
    private final static Messenger processSingleClientMessenger=new Messenger(processResponseHandler);
    private Context context;
    private IRequest request;
    private ITarget target;
    private IPCRemoteCallListener listener;
    private ICallerConfig callerConfig;
    private Messenger replyMessenger;
    private @RemoteAction int action=RemoteAction.REMOTE_ACTION_CREATE_REMOTE_IPC_CHAT;
    protected IPCRemoteConnectorImpl(Context context){
        this.context=context;
    }
    public static IPCRemoteConnector.Builder newBuilder(Context context){
        return new IPCRemoteConnectorImpl(context);
    }
    public static boolean connect(Context context, String app, AppIPCServiceConnectionCallback connectionCallback){
        Intent intent;
        boolean isBindCurrAppMainProcess= TextUtils.isEmpty(app) || context.getPackageName().equals(app);
        if (isBindCurrAppMainProcess) {
            //Log.e("ipc","------------------->连接当前App服务端："+app);
            intent = new Intent(context, AppIPCService.class);
        }
        else {
            //跨App调起事件分发服务
            //Log.e("ipc","------------------->连接其他App服务端："+app);
            intent = new Intent(app + ".ipc");
            intent.setPackage(app);
        }
        return context.bindService(intent, connectionCallback, Context.BIND_AUTO_CREATE);
    }

    @Override
    public IRequest request() {
        return this.request;
    }

    @Override
    public ITarget target() {
        return this.target;
    }

    @Override
    public IPCRemoteCallListener listener() {
        return this.listener;
    }

    @Override
    public ICallerConfig callerConfig() {
        return this.callerConfig;
    }

    @Override
    public int action() {
        return this.action;
    }

    @Override
    public Messenger replyMessenger() {
        if (action()==RemoteAction.REMOTE_ACTION_CREATE_REMOTE_IPC_CHAT){
            return new Messenger(new RemoteCallbackHandler(request,this,target));
        }
        else if (action()==RemoteAction.REMOTE_ACTION_REGISTER_TO_MESSENGERS_POOL){
            return processSingleClientMessenger;
        }
        else if (action()==RemoteAction.REMOTE_ACTION_CREATE_REMOTE_IPC_CUSTOMER){
            return replyMessenger;
        }
        return null;
    }

    @Override
    public void execute() throws Exception {

        if (target==null){
            target=TargetImpl.newBuilder()
                    .toApp(context.getPackageName())
                    .toProcess(Utils.getCurrAppProccessName(context))
                    .build();
            //throw new Exception("ur ITarget is null !!!");
        }
        if (request==null){
            request=RequestImpl.newInstance("EMPTY_DATA_REQUEST");
            //throw new Exception("ur IRequest is null !!!");
        }
        if (listener!=null){
            request=listener.onRemoteCallInputRequest(request,target);
        }
        Message message=Message.obtain();
        Bundle bundle=new Bundle();
        bundle.setClassLoader(getClass().getClassLoader());
        bundle.putParcelable(CommonConstant.REMOTE_BUNDLE_REQUEST,request);
        bundle.putParcelable(CommonConstant.REMOTE_BUNDLE_TARGET,target);
        bundle.putInt(CommonConstant.REMOTE_BUNDLE_ACTION,action);
        bundle.putParcelable(CommonConstant.REMOTE_BUNDLE_CALLER_CONFIG,callerConfig);
        message.setData(bundle);
        message.replyTo=replyMessenger();
        IRequest finalRequest = request;
        connect(context, target.toApp(), new AppIPCServiceConnectionCallback() {
            @Override
            public void onGetMessenger(ComponentName name, Messenger serverMessenger) {
                if (listener!=null){
                    listener.onRemoteCallConnectedServer(name,serverMessenger,target);
                }
                if (serverMessenger!=null){
                    try {
                        serverMessenger.send(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (listener!=null){
                            listener.onRemoteCallException(finalRequest,e,target);
                        }
                    }
                }
            }
        });
    }

    @Override
    public Builder toBuilder() {
        return this;
    }

    @Override
    public Builder request(IRequest request) {
        this.request=request;
        return null;
    }

    @Override
    public Builder target(ITarget target) {
        this.target=target;
        return this;
    }

    @Override
    public Builder listener(IPCRemoteCallListener listener) {
        this.listener=listener;
        return this;
    }

    @Override
    public Builder action(int action) {
        this.action=action;
        return this;
    }

    @Override
    public Builder replyMessenger(Messenger replyMessenger) {
        this.replyMessenger=replyMessenger;
        return this;
    }

    @Override
    public Builder callerConfig(ICallerConfig callerConfig) {
        this.callerConfig=callerConfig;
        return this;
    }

    @Override
    public IPCRemoteConnector build() {
        return this;
    }

    static class RemoteCallbackHandler extends Handler {
        private WeakReference<IPCRemoteConnector> remoteConnectorWeakReference;
        private WeakReference<IRequest> requestWeakReference;
        private WeakReference<ITarget> targetWeakReference;
        protected RemoteCallbackHandler(IRequest request,IPCRemoteConnector remoteConnector,ITarget target){
            remoteConnectorWeakReference=new WeakReference<>(remoteConnector);
            requestWeakReference=new WeakReference<>(request);
            targetWeakReference=new WeakReference<>(target);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            IRequest request=null;
            IPCRemoteConnector remoteConnector=null;
            ITarget target=null;
            if (requestWeakReference!=null){
                request=requestWeakReference.get();
            }
            if (remoteConnectorWeakReference!=null){
                remoteConnector=remoteConnectorWeakReference.get();
            }
            if (targetWeakReference!=null){
                target=targetWeakReference.get();
            }
            Bundle bundle=msg.getData();
            if (bundle!=null){
                int state= msg.what;
                if (state==RemoteActionResultState.REMOTE_ACTION_RESULT_STATE_SUCCESS){
                    if (remoteConnector!=null){
                        IResponse response=bundle.getParcelable(CommonConstant.REMOTE_BUNDLE_RESPONSE);
                        IRequest finalRequest = request;
                        IPCRemoteCallListener listener=remoteConnector.listener();
                        if (listener!=null){
                            ITarget finalTarget = target;
                            IPCRemoteConnector finalRemoteConnector = remoteConnector;
                            listener.onRemoteCallReceivedResponse(response, new IRequestSession() {
                                @Override
                                public boolean replyRequestData(IDataContainer dataContainer, ICallerConfig callerConfig) {
                                    //外部调用方再次与远程组件进行通信
                                    finalRequest.toBuilder().dataContainer(dataContainer).build();
                                    return replyRequest(finalRequest,callerConfig);
                                }

                                @Override
                                public boolean replyRequest(IRequest request, ICallerConfig callerConfig) {
                                    //外部调用方再次与远程组件进行通信
                                    try {
                                        finalRemoteConnector.toBuilder()
                                                .request(request)
                                                .target(finalTarget)
                                                .callerConfig(callerConfig)
                                                .build()
                                                .execute();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    return false;
                                }
                            }, target);
                        }

                    }
                }
                else if(state==RemoteActionResultState.REMOTE_ACTION_RESULT_STATE_FAILURE){
                    if (remoteConnector!=null){
                        Throwable throwable= (Throwable) bundle.getSerializable(CommonConstant.REMOTE_BUNDLE_THROWABLE);
                        IPCRemoteCallListener listener=remoteConnector.listener();
                        if (listener!=null){
                            listener.onRemoteCallException(request,throwable,target);
                        }

                    }
                }
            }
        }
    }
}

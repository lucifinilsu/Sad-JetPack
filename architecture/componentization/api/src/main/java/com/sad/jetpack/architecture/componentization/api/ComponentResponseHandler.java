package com.sad.jetpack.architecture.componentization.api;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

import androidx.annotation.NonNull;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

final class ComponentResponseHandler extends Handler {
    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        Bundle bundle=msg.getData();
        Messenger replyMessenger=msg.replyTo;
        if (replyMessenger!=null){
            if (bundle!=null){
                bundle.setClassLoader(IPCRemoteConnectorImpl.class.getClassLoader());
                IRequest request=bundle.getParcelable(CommonConstant.REMOTE_BUNDLE_REQUEST);
                ICallerConfig callerConfig=bundle.getParcelable(CommonConstant.REMOTE_BUNDLE_CALLER_CONFIG);
                ITarget target=bundle.getParcelable(CommonConstant.REMOTE_BUNDLE_TARGET);
                int action=bundle.getInt(CommonConstant.REMOTE_BUNDLE_ACTION);
                int processorMode=target.processorMode();
                InstancesRepositoryFactory instancesRepositoryFactory=bundle.getParcelable(CommonConstant.REMOTE_BUNDLE_CALLER_INSTANCES_REPOSITORY_FACTORY);
                IComponentsCluster cluster=InternalComponentCluster.newInstance(InternalContextHolder.get().getContext())
                        .instancesRepositoryFactory(instancesRepositoryFactory)
                        ;
                InstancesRepository repository=cluster.repository(target.id());
                if (processorMode==ProcessorMode.PROCESSOR_MODE_SINGLE){
                    IComponentCallable componentCallable=repository.firstComponentCallableInstance();
                    componentCallable.toBuilder()
                            .callerConfig(callerConfig)
                            .listener(new IComponentCallListener() {
                                @Override
                                public boolean onComponentReceivedResponse(IResponse response, IRequestSession session, String componentId) {
                                    try {
                                        bundle.setClassLoader(ComponentResponseHandler.this.getClass().getClassLoader());
                                        bundle.putParcelable(CommonConstant.REMOTE_BUNDLE_RESPONSE,response);
                                        msg.what=RemoteActionResultState.REMOTE_ACTION_RESULT_STATE_SUCCESS;
                                        msg.setData(bundle);
                                        Message message=Message.obtain();
                                        message.copyFrom(msg);
                                        replyMessenger.send(message);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    return false;
                                }

                                @Override
                                public void onComponentException(IRequest request, Throwable throwable, String componentId) {
                                    try {
                                        bundle.setClassLoader(ComponentResponseHandler.this.getClass().getClassLoader());
                                        bundle.putSerializable(CommonConstant.REMOTE_BUNDLE_THROWABLE,throwable);
                                        msg.what=RemoteActionResultState.REMOTE_ACTION_RESULT_STATE_FAILURE;
                                        msg.setData(bundle);
                                        Message message=Message.obtain();
                                        message.copyFrom(msg);
                                        replyMessenger.send(message);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .build()
                            .call(request);
                            ;
                }
                else {
                    IComponentProcessor.Builder processorBuilder=(processorMode==ProcessorMode.PROCESSOR_MODE_CONCURRENCY?
                            InternalComponentConcurrencyProcessor.newBuilder()
                            :
                            InternalComponentSequenceProcessor.newBuilder());
                    processorBuilder
                            .callerConfig(callerConfig)
                            .listenerCrossed(false)
                            .listener(new IComponentProcessorCallListener() {
                                @Override
                                public boolean onProcessorReceivedResponse(IResponse response, String processorId) {
                                    try {
                                        bundle.setClassLoader(ComponentResponseHandler.this.getClass().getClassLoader());
                                        bundle.putParcelable(CommonConstant.REMOTE_BUNDLE_RESPONSE,response);
                                        msg.what=RemoteActionResultState.REMOTE_ACTION_RESULT_STATE_SUCCESS;
                                        msg.setData(bundle);
                                        LogcatUtils.e("ipc","--------->msg="+msg.toString());
                                        Message message=Message.obtain();
                                        message.copyFrom(msg);
                                        LogcatUtils.e("ipc","--------->message="+message.toString());
                                        replyMessenger.send(message);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    return false;
                                }

                                @Override
                                public IResponse onProcessorMergeResponses(ConcurrentLinkedHashMap<IResponse, String> responses, String processorId) {
                                    return null;
                                }

                                @Override
                                public void onProcessorException(IRequest request, Throwable throwable, String processorId) {
                                    try {
                                        bundle.setClassLoader(ComponentResponseHandler.this.getClass().getClassLoader());
                                        bundle.putSerializable(CommonConstant.REMOTE_BUNDLE_THROWABLE,throwable);
                                        msg.what=RemoteActionResultState.REMOTE_ACTION_RESULT_STATE_FAILURE;
                                        msg.setData(bundle);
                                        Message message=Message.obtain();
                                        message.copyFrom(msg);
                                        LogcatUtils.e("ipc","--------->message="+message.toString());
                                        replyMessenger.send(message);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
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
                                public IResponse onChildProcessorMergeResponses(ConcurrentLinkedHashMap<IResponse, String> responses, IResponse childResponse, String childProcessorId) {
                                    return null;
                                }

                                @Override
                                public void onChildProcessorException(IRequest request, Throwable throwable, String childProcessorId) {

                                }
                            })
                            .build()
                            .join(repository.componentCallableInstances())
                            .submit(request);
                            ;
                }
            }
        }
    }
}

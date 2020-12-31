package com.sad.jetpack.architecture.componentization.api;

import com.sad.core.async.SADHandlerAssistant;
import com.sad.core.async.SADTaskSchedulerClient;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

final class InternalComponentCallable implements IComponentCallable,IComponentCallable.Builder{
    private IComponent component;
    private ICallerConfig callerConfig;
    private IComponentCallListener listener;
    private String id="";

    protected static IComponentCallable with(IComponent component){
        return new InternalComponentCallable(component);
    }
    protected static IComponentCallable.Builder newBuilder(IComponent component){
        return new InternalComponentCallable(component);
    }
    protected InternalComponentCallable(IComponent component){
        this.component=component;
    }

    @Override
    public String componentId() {
        return this.id;
    }

    @Override
    public IComponent component() {
        return component;
    }

    @Override
    public ICallerConfig callerConfig() {
        return this.callerConfig;
    }

    @Override
    public IComponentCallListener listener() {
        return this.listener;
    }

    private AtomicBoolean isComponentDone;
    private AtomicBoolean isTimeout ;
    private ScheduledFuture timeoutFuture;
    private void startTimeout(IRequest request, long timeout, IComponentCallListener callback){
        if (timeout>0){
            timeoutFuture= SADTaskSchedulerClient.executeScheduledTask(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    isTimeout.getAndSet(true);
                    if (!isComponentDone.get()){
                        if (callback!=null){
                            Exception e= new TimeoutException("the task of the component whose url is '"+id+"' is timeout !!!");
                            callback.onComponentException(request,e,id);
                        }
                    }
                    return null;
                }
            },timeout);
        }
    }
    private void finishTimeout(){
        if (timeoutFuture!=null && !timeoutFuture.isCancelled() && !timeoutFuture.isDone()){
            timeoutFuture.cancel(true);
        }
    }
    @Override
    public void call(IRequest request) {
        isComponentDone =new AtomicBoolean(false);
        isTimeout= new AtomicBoolean(false);
        finishTimeout();
        boolean needDelay=(callerConfig!=null && callerConfig.delay()>0);
        if (listener!=null){
            request=listener.onComponentInputRequest(request,id);
        }
        IRequest r=request;
        if (needDelay){
            SADHandlerAssistant.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    doCall(r,callerConfig, listener);
                }
            }, callerConfig.delay());
        }
        else {
            doCall(r,callerConfig, listener);
        }
    }

    @Override
    public Builder toBuilder() {
        return this;
    }

    private void doCall(IRequest request, ICallerConfig config, IComponentCallListener listener){
        try {
            if (component==null){
                throw new Exception("ur component instance is null !!!");
            }
            boolean needTimeout=(config!=null && config.timeout()>0);
            if (needTimeout){
                startTimeout(request,config.timeout(),listener);
            }
            IRequestSession requestSession= new IRequestSession() {
                @Override
                public boolean replyRequestData(IBody body,ICallerConfig callerConfig) {
                    return replyRequest(request.toBuilder().body(body).build(),callerConfig);
                }

                @Override
                public boolean replyRequest(IRequest request,ICallerConfig callerConfig) {
                    callerConfig(config);
                    call(request);
                    return false;
                }
            };
            component.onCall(request, new IResponseSession() {
                @Override
                public boolean postResponseData(IBody body) {
                    IResponse componentResponse= ResponseImpl
                            .newBuilder()
                            .body(body)
                            .request(request)
                            .build();
                    return postResponseData(componentResponse);
                }

                @Override
                public boolean postResponseData(IResponse response) {
                    isComponentDone.getAndSet(true);
                    finishTimeout();
                    if (listener!=null && (!needTimeout || !isTimeout.get())){
                        listener.onComponentReceivedResponse(response,requestSession, id);
                    }
                    return false;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            if (listener!=null){
                listener.onComponentException(request,e,id);
            }
        }

    }

    @Override
    public Builder callerConfig(ICallerConfig config) {
        this.callerConfig=config;
        return this;
    }

    @Override
    public Builder listener(IComponentCallListener listener) {
        this.listener=listener;
        return this;
    }

    @Override
    public Builder componentId(String id) {
        this.id=id;
        return this;
    }

    @Override
    public IComponentCallable build() {
        return this;
    }
}

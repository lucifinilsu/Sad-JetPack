package com.sad.jetpack.architecture.componentization.api2;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

import androidx.annotation.NonNull;

import com.sad.core.async.SADTaskSchedulerClient;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

final class InternalComponentSingleCallable implements IComponentCallable{
    private IComponent component;
    private AtomicBoolean isTimeout;
    private ScheduledFuture timeoutFuture;
    private Context context;
    private long delay=-1;
    protected InternalComponentSingleCallable(Context context,IComponent component){
        this.component=component;
        this.context=context;
    }

    private void startTimeout(long timeout){
        isTimeout=new AtomicBoolean(false);
        if (timeout>0){

            timeoutFuture= SADTaskSchedulerClient.executeScheduledTask(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    isTimeout.getAndSet(true);
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
    public IComponent component() {
        return this.component;
    }

    @Override
    public <T> T call(Message message) {
        return call(message,null);
    }

    @Override
    public <T> T call(Message message, IPCResultCallback callback) {
        return call(message,-1,callback);
    }

    @Override
    public <T> T call(Message message, long timeout, IPCResultCallback callback) {
        IPCMessageTransmissionConfig transmissionConfig=MessageCreator.standardMessage(context,message,component.instanceOrgUrl(),timeout,delay,IPCTarget.SINGLE);
        try {
            startTimeout(timeout);
            if (isTimeout.get()){
                throw new TimeoutException("the processor of the unit whose type is SINGLE is timeout !!!");
            }
            IPCLauncher launcher=IPCLauncherImpl.newInstance(context).transmissionConfig(transmissionConfig);
            message.replyTo=new Messenger(new ProxyHandler(callback,transmissionConfig));
            T t=component.onCall(message,launcher);
            return t;
        }catch (Exception e){
            e.printStackTrace();
            message.obj=e;
            if (callback!=null){
                callback.onException(transmissionConfig,e);
            }
        }
        return null;
    }

    @Override
    public void call(Message message, long timeout, long delay, IPCResultCallback callback) {
        this.delay=delay;
        if (delay>0){
            SADTaskSchedulerClient.executeScheduledTask(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    isTimeout.getAndSet(true);
                    return InternalComponentSingleCallable.this.call(message,timeout,callback);
                }
            },delay);
        }
        else {
            call(message,timeout,callback);
        }
    }

    private static class ProxyHandler extends Handler {
        private WeakReference<IPCResultCallback> callbackWeakReferenceWeakReference;
        private WeakReference<IPCMessageTransmissionConfig> transmissionConfigWeakReference;
        ProxyHandler(
                IPCResultCallback callback,
                IPCMessageTransmissionConfig transmissionConfig
        ){

            this.callbackWeakReferenceWeakReference =new WeakReference<IPCResultCallback>(callback);
            this.transmissionConfigWeakReference =new WeakReference<IPCMessageTransmissionConfig>(transmissionConfig);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            IPCResultCallback callback=null;
            IPCMessageTransmissionConfig transmissionConfig=null;
            if (callbackWeakReferenceWeakReference !=null){
                callback= callbackWeakReferenceWeakReference.get();
            }
            if (transmissionConfigWeakReference !=null){
                transmissionConfig= transmissionConfigWeakReference.get();
            }
            if (callback!=null){
                callback.onDone(msg,transmissionConfig);
            }
        }
    }
}

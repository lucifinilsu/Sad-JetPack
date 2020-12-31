package com.sad.jetpack.architecture.componentization.api;


import android.os.Handler;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.sad.core.async.ISADTaskProccessListener;
import com.sad.core.async.SADTaskRunnable;
import com.sad.core.async.SADTaskSchedulerClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

final class InternalComponentSequenceProcessor extends AbsInternalComponentProcessor{
    private CountDownLatch countDownLatch;
    private IResponse response;
    //private ConcurrentLinkedHashMap<IResponse,String> responses;
    protected static IComponentProcessor.Builder newBuilder(){
        return new InternalComponentSequenceProcessor();
    }
    protected static IComponentProcessor newInstance(){
        return new InternalComponentSequenceProcessor();
    }
    private InternalComponentSequenceProcessor(){
        response= ResponseImpl.newInstance();
    }
    @Override
    public void submit(IRequest request) {
        if (callListener!=null){
            request=callListener.onProcessorInputRequest(request,processorId);
        }
        IRequest r=request;
        if (units.isEmpty()){
            Exception e= new Exception("the units of ur remote task'target is empty !!!");
            if (callListener!=null){
                callListener.onProcessorException(request,e,processorId);
            }
            return;
        }
        countDownLatch=new CountDownLatch(units.size());
        SADTaskSchedulerClient.newInstance().execute(new SADTaskRunnable<IResponse>("PROCESSOR_COUNTDOWN", new ISADTaskProccessListener<IResponse>() {
            @Override
            public void onSuccess(IResponse result) {
                if (callListener!=null){
                    callListener.onProcessorReceivedResponse(result, processorId);
                }
            }

            @Override
            public void onFail(Throwable throwable) {
                if (callListener!=null){
                    callListener.onProcessorException(r,throwable,processorId);
                }
            }

            @Override
            public void onCancel() {

            }
        }) {
            @Override
            public IResponse doInBackground() throws Exception {
                if (needCheckTimeout()){
                    if (countDownLatch.await(callerConfig.timeout(), TimeUnit.MILLISECONDS)){
                        return response;
                    }
                    else {
                        throw new TimeoutException("the task of the processor whose id is '"+processorId+"' is timeout !!!");
                    }
                }
                else {
                    countDownLatch.await();
                }
                return response;
            }
        });
        if (needDelay()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doSubmit(r);
                }
            },callerConfig.delay());
        }
        else {
            doSubmit(r);
        }
    }
    private AtomicInteger currIndex=new AtomicInteger(0);
    private void doSubmit(IRequest request){
        try {
            Object o=units.get(currIndex.get());
            IRequest r = request;
            if (o instanceof  IComponentCallable){
                IComponentCallable callable= (IComponentCallable) o;
                IComponentCallListener listenerSelf=callable.listener();
                IComponentCallable callablePro=callable.toBuilder()
                        .listener(new IComponentCallListener() {
                            @Override
                            public IRequest onComponentInputRequest(IRequest request, String componentId) {
                                if (listenerSelf!=null){
                                    request= listenerSelf.onComponentInputRequest(request,componentId);
                                }
                                if (callListener!=null){
                                    request = callListener.onChildComponentInputRequest(request,componentId);
                                }
                                return request;
                            }

                            @Override
                            public boolean onComponentReceivedResponse(IResponse response, IRequestSession session, String componentId) {
                                if (listenerSelf!=null){
                                    listenerSelf.onComponentReceivedResponse(response,session,componentId);
                                }
                                if (callListener!=null){
                                    callListener.onChildComponentReceivedResponse(response,session,componentId);
                                }
                                InternalComponentSequenceProcessor.this.response=response;
                                countDownLatch.countDown();
                                if (currIndex.get()<units.size()-1){
                                    currIndex.set(currIndex.get()+1);
                                    IRequest nextRequest=response.request().toBuilder()
                                            .body(response.body())
                                            .build()
                                            ;
                                    doSubmit(nextRequest);
                                }
                                return false;
                            }

                            @Override
                            public void onComponentException(IRequest request, Throwable throwable,String componentId) {
                                if (listenerSelf!=null){
                                    listenerSelf.onComponentException(request,throwable,componentId);
                                }
                                if (callListener!=null){
                                    callListener.onChildComponentException(request,throwable,componentId);
                                }
                                if (currIndex.get()<units.size()-1){
                                    for (int i = currIndex.get(); i < units.size()-1; i++) {
                                        countDownLatch.countDown();
                                    }
                                }
                                else {
                                    countDownLatch.countDown();
                                }

                            }
                        })
                        .build()
                        ;
                callablePro.call(r);
            }
            else if (o instanceof  IComponentProcessor){
                IComponentProcessor processor= (IComponentProcessor) o;
                IComponentProcessorCallListener listenerSelf=processor.listener();
                IComponentProcessor processorPro=processor.toBuilder()
                        .listener(new IComponentProcessorCallListener() {
                            @Override
                            public IRequest onProcessorInputRequest(IRequest request, String processorId) {
                                if (listenerSelf!=null){
                                    request=listenerSelf.onProcessorInputRequest(request,processorId);
                                }
                                if (callListener!=null){
                                    request=callListener.onChildProcessorInputRequest(request,processorId);
                                }
                                return request;
                            }

                            @Override
                            public IRequest onChildComponentInputRequest(IRequest request, String componentId) {
                                if (listenerSelf!=null){
                                    request=listenerSelf.onChildComponentInputRequest(request,componentId);
                                }
                                if (listenerCrossed && callListener!=null){
                                    request = callListener.onChildComponentInputRequest(request,componentId);
                                }
                                return request;
                            }

                            @Override
                            public boolean onChildComponentReceivedResponse(IResponse response, IRequestSession session, String componentId) {
                                if (listenerSelf!=null){
                                    listenerSelf.onChildComponentReceivedResponse(response,session,componentId);
                                }
                                if (listenerCrossed && callListener!=null){
                                    callListener.onChildComponentReceivedResponse(response,session,componentId);
                                }
                                return false;
                            }

                            @Override
                            public void onChildComponentException(IRequest request, Throwable throwable, String componentId) {
                                if (listenerSelf!=null){
                                    listenerSelf.onChildComponentException(request,throwable,componentId);
                                }
                                if (listenerCrossed && callListener!=null){
                                    callListener.onChildComponentException(request,throwable,componentId);
                                }
                            }

                            @Override
                            public IRequest onChildProcessorInputRequest(IRequest request, String processorId) {
                                if (listenerSelf!=null){
                                    request=listenerSelf.onChildProcessorInputRequest(request,processorId);
                                }
                                if (listenerCrossed && callListener!=null){
                                    request=callListener.onChildProcessorInputRequest(request,processorId);
                                }
                                return request;
                            }

                            @Override
                            public boolean onChildProcessorReceivedResponse(IResponse response, String childProcessorId) {
                                if (listenerSelf!=null){
                                    listenerSelf.onChildProcessorReceivedResponse(response,childProcessorId);
                                }
                                if (listenerCrossed && callListener!=null){
                                    callListener.onChildProcessorReceivedResponse(response,childProcessorId);
                                }
                                return false;
                            }

                            @Override
                            public IResponse onChildProcessorMergeResponses(ConcurrentLinkedHashMap<IResponse, String> responses, IResponse childResponse, String childProcessorId) {
                                IResponse response=childResponse;
                                if (listenerSelf!=null){
                                    response=listenerSelf.onChildProcessorMergeResponses(responses,response,childProcessorId);
                                }
                                if (listenerCrossed && callListener!=null){
                                    response=callListener.onChildProcessorMergeResponses(responses,response,childProcessorId);
                                }
                                return response;
                            }

                            @Override
                            public void onChildProcessorException(IRequest request, Throwable throwable, String childProcessorId) {
                                if (listenerSelf!=null){
                                    listenerSelf.onChildProcessorException(request,throwable,childProcessorId);
                                }
                                if (listenerCrossed && callListener!=null){
                                    callListener.onChildProcessorException(request,throwable,childProcessorId);
                                }
                            }

                            @Override
                            public boolean onProcessorReceivedResponse(IResponse response, String processorId) {
                                if (listenerSelf!=null){
                                    listenerSelf.onProcessorReceivedResponse(response,processorId);
                                }
                                if (callListener!=null){
                                    callListener.onChildProcessorReceivedResponse(response,processorId);
                                }
                                InternalComponentSequenceProcessor.this.response=response;
                                countDownLatch.countDown();
                                return false;
                            }

                            @Override
                            public IResponse onProcessorMergeResponses(ConcurrentLinkedHashMap<IResponse, String> responses,String processorId) {
                                IResponse response=null;
                                if (listenerSelf!=null){
                                    response = listenerSelf.onProcessorMergeResponses(responses,processorId);
                                }
                                if (callListener!=null){
                                    response=callListener.onChildProcessorMergeResponses(responses,response,processorId);
                                }
                                return response;
                            }

                            @Override
                            public void onProcessorException(IRequest request, Throwable throwable, String processorId) {
                                if (listenerSelf!=null){
                                    listenerSelf.onProcessorException(request,throwable,processorId);
                                }
                                if (callListener!=null){
                                    callListener.onChildProcessorException(request,throwable,processorId);
                                }
                                if (currIndex.get()<units.size()-1){
                                    for (int i = currIndex.get(); i < units.size()-1; i++) {
                                        countDownLatch.countDown();
                                    }
                                }
                                else {
                                    countDownLatch.countDown();
                                }
                            }

                        })
                        .build();
                processorPro.submit(r);
            }
        }catch (Exception e){
            e.printStackTrace();
            if (callListener!=null){
                callListener.onProcessorException(request,e,processorId);
            }
        }
    }
}

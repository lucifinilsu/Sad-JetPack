package com.sad.jetpack.architecture.componentization.api;


import android.os.Handler;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.sad.core.async.ISADTaskProccessListener;
import com.sad.core.async.SADTaskRunnable;
import com.sad.core.async.SADTaskSchedulerClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

final class InternalComponentSequenceProcessor extends AbsInternalComponentProcessor {
    private CountDownLatch countDownLatch;
    private IResponse response;
    private AtomicReference<Throwable> currThrowable=new AtomicReference<>();

    //private ConcurrentLinkedHashMap<IResponse,String> responses;
    protected static IComponentProcessor.Builder newBuilder(String id){
        return new InternalComponentSequenceProcessor(id);
    }
    protected static IComponentProcessor newInstance(String id){
        return new InternalComponentSequenceProcessor(id);
    }
    private InternalComponentSequenceProcessor(String id){
        this.processorId=id;
        response= ResponseImpl.newInstance();
    }

    @Override
    public void onBackTrackResponse(IComponentChain chain) throws Exception {
        LogcatUtils.internalLog(">>>>Processor回溯");
        //先进行内部回溯，完成后再回溯上级，注意，内部回溯依然要根据拦截情况设定回溯路径。
        callInternalChain(units,isIntercepted.get(),indexIntercepted.get(),chain.parentId(), chain.response(), new IComponentChain.IComponentChainTerminalCallback() {
            @Override
            public void onLast(IResponse response, String id,boolean intercepted) throws Exception{
                chain.proceedResponse(response,chain.parentId());
            }
        });
    }

    private void callInternalChain(
            List<Object> o_units,
            boolean intercepted,
            int indexIntercepted,
            String id,
            IResponse response,
            IComponentChain.IComponentChainTerminalCallback chainTerminalCallback) throws Exception{
        List<Object> units=o_units.subList(0,indexIntercepted+1);
        Object[] units_reSort=new Object[units.size()];
        //任务重新倒序
        for (Object o:units
             ) {
            units_reSort[units.size()-1-units.indexOf(o)]=o;
        }
        List<Object> units_reSort_list=new ArrayList<>(Arrays.asList(units_reSort));

        InternalComponentChain chain=new InternalComponentChain(units_reSort_list,intercepted);
        chain.setResponse(response);
        chain.setId(id);
        chain.setTerminalCallback(chainTerminalCallback);
        chain.proceedResponse(response,id);

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
        //任务排序
        Collections.sort(units, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof ISortable && o2 instanceof ISortable){
                    return ((ISortable) o2).priority()-((ISortable) o1).priority();
                }
                return 0;
            }
        });
        indexIntercepted.set(units.size()-1);
        countDownLatch=new CountDownLatch(units.size());
        SADTaskSchedulerClient.newInstance().execute(new SADTaskRunnable<IResponse>("PROCESSOR_COUNTDOWN", new ISADTaskProccessListener<IResponse>() {
            @Override
            public void onSuccess(IResponse result) {
                try {
                    LogcatUtils.internalLog(">>>拦截信息：index="+indexIntercepted.get()+",intercepted="+isIntercepted.get());
                    callInternalChain(units,isIntercepted.get(),indexIntercepted.get(),processorId,result,new IComponentChain.IComponentChainTerminalCallback() {
                        @Override
                        public void onLast(IResponse response, String id,boolean intercepted) throws Exception{
                            if (callListener!=null){
                                callListener.onProcessorReceivedResponse(response, id,intercepted);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callListener!=null){
                        callListener.onProcessorException(r,e,processorId);
                    }
                }
            }

            @Override
            public void onFail(Throwable throwable) {
                currThrowable.set(null);
                if (callListener!=null){
                    callListener.onProcessorException(r,throwable,processorId);
                }
            }

            @Override
            public void onCancel() {

            }
        }) {
            @Override
            public IResponse doInBackground() throws Exception{
                if (needCheckTimeout()){
                    if (countDownLatch.await(callerConfig.timeout(), TimeUnit.MILLISECONDS)){
                        Throwable throwable=currThrowable.get();
                        if (throwable!=null){
                            throw (Exception) throwable;
                        }
                        return response;
                    }
                    else {
                        throw new TimeoutException("the task of the processor whose id is '"+processorId+"' is timeout !!!");
                    }
                }
                else {
                    countDownLatch.await();
                    Throwable throwable=currThrowable.get();
                    if (throwable!=null){
                        throw (Exception) throwable;
                    }
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
                            public boolean onComponentReceivedResponse(IResponse response, IRequestSession session, String componentId,boolean intercepted) {
                                if (listenerSelf!=null){
                                    listenerSelf.onComponentReceivedResponse(response,session,componentId,intercepted);
                                }
                                if (callListener!=null){
                                    callListener.onChildComponentReceivedResponse(response,session,componentId,intercepted);
                                }
                                InternalComponentSequenceProcessor.this.response=response;
                                isIntercepted.set(intercepted);
                                if (!intercepted){
                                    countDownLatch.countDown();
                                    if (currIndex.get()<units.size()-1){
                                        currIndex.set(currIndex.get()+1);
                                        IRequest nextRequest=response.request()
                                                .toBuilder()
                                                .previousResponse(response)
                                                .build()
                                                ;
                                        doSubmit(nextRequest);
                                    }
                                }
                                else {
                                    int start=currIndex.get();
                                    indexIntercepted.set(start);
                                    LogcatUtils.internalLog(">>>出现拦截点：index="+indexIntercepted.get());
                                    for (int i = start; i < units.size(); i++) {
                                        currIndex.set(i);
                                        LogcatUtils.internalLog(">>>强制关停线程等待计数：i="+i+",total="+units.size());
                                        countDownLatch.countDown();
                                    }

                                }
                                return false;
                            }

                            @Override
                            public void onComponentException(IRequest request, Throwable throwable,String componentId) {
                                currThrowable.set(throwable);
                                if (listenerSelf!=null){
                                    listenerSelf.onComponentException(request,throwable,componentId);
                                }
                                if (callListener!=null){
                                    callListener.onChildComponentException(request,throwable,componentId);
                                }

                                int start=currIndex.get();
                                indexIntercepted.set(start);
                                for (int i = start; i < units.size(); i++) {
                                    currIndex.set(i);
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
                            public boolean onChildComponentReceivedResponse(IResponse response, IRequestSession session, String componentId,boolean intercepted) {
                                if (listenerSelf!=null){
                                    listenerSelf.onChildComponentReceivedResponse(response,session,componentId,intercepted);
                                }
                                if (listenerCrossed && callListener!=null){
                                    callListener.onChildComponentReceivedResponse(response,session,componentId,intercepted);
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
                            public boolean onChildProcessorReceivedResponse(IResponse response, String childProcessorId,boolean intercepted) {
                                if (listenerSelf!=null){
                                    listenerSelf.onChildProcessorReceivedResponse(response,childProcessorId,intercepted);
                                }
                                if (listenerCrossed && callListener!=null){
                                    callListener.onChildProcessorReceivedResponse(response,childProcessorId,intercepted);
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
                            public boolean onProcessorReceivedResponse(IResponse response, String processorId,boolean intercepted) {
                                if (listenerSelf!=null){
                                    listenerSelf.onProcessorReceivedResponse(response,processorId,intercepted);
                                }
                                if (callListener!=null){
                                    callListener.onChildProcessorReceivedResponse(response,processorId,intercepted);
                                }
                                InternalComponentSequenceProcessor.this.response=response;
                                isIntercepted.set(intercepted);
                                if (!intercepted) {
                                    countDownLatch.countDown();
                                    if (currIndex.get() < units.size() - 1) {
                                        currIndex.set(currIndex.get() + 1);
                                        IRequest nextRequest = response.request()
                                                .toBuilder()
                                                .previousResponse(response)
                                                .build();
                                        doSubmit(nextRequest);
                                    }
                                }
                                else {
                                    int start=currIndex.get();
                                    indexIntercepted.set(start);
                                    LogcatUtils.internalLog(">>>出现拦截点：index="+indexIntercepted.get());
                                    for (int i = start; i < units.size(); i++) {
                                        currIndex.set(i);
                                        LogcatUtils.internalLog(">>>强制关停线程等待计数：i="+i+",total="+units.size());
                                        countDownLatch.countDown();
                                    }
                                }
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
                                currThrowable.set(throwable);
                                if (listenerSelf!=null){
                                    listenerSelf.onProcessorException(request,throwable,processorId);
                                }
                                if (callListener!=null){
                                    callListener.onChildProcessorException(request,throwable,processorId);
                                }
                                int start=currIndex.get();
                                indexIntercepted.set(start);
                                for (int i = start; i < units.size(); i++) {
                                    currIndex.set(i);
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

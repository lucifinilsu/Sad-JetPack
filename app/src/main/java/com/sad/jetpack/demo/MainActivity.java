package com.sad.jetpack.demo;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.sad.jetpack.architecture.appgo.api.AppGo;
import com.sad.jetpack.architecture.componentization.annotation.IPCChat;
import com.sad.jetpack.architecture.componentization.api.ExposedServiceManager;
import com.sad.jetpack.architecture.componentization.api.IProceedListener;
import com.sad.jetpack.architecture.componentization.api.ICluster;
import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceManagerAsync;
import com.sad.jetpack.architecture.componentization.api.IPCMessenger;
import com.sad.jetpack.architecture.componentization.api.IPCSession;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IPerformer;
import com.sad.jetpack.architecture.componentization.api.SCore;
import com.sad.jetpack.architecture.componentization.api.impl.DataCarrierImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.sad.jetpack.architecture.componentization.api.ILocalProcessor.*;

public class MainActivity extends AppCompatActivity {
    @IPCChat(url = {"test://ipc/chat/ttt","test://ipc/chat/sss"},priority = 996)
    public void onTestIPCChat(IPCMessenger messenger){
        Log.e("sad-jetpack","------------->onTestIPCChat收到信息:"+messenger.extraMessage().data());
        new Thread(){
            @Override
            public void run() {
                IDataCarrier dataCarrier=messenger.extraMessage();
                dataCarrier.creator().data("onTestIPCChat运行完毕").create();
                /*DefaultDataContainer dataContainer=dataCarrier.data();
                dataContainer.add(messenger.messengerId(),"onTestIPCChat运行完毕");
                dataCarrier.creator().data(dataContainer).create();*/
                messenger.reply(dataCarrier);
            }
        }.start();

    }
    @IPCChat(url = {"test://ipc/chat/sss"},priority = 995)
    public void onTestIPCChat2(IPCMessenger messenger){
        Log.e("sad-jetpack","------------->onTestIPCChat2收到信息:"+messenger.extraMessage().data());
        new Thread(){
            @Override
            public void run() {
                IDataCarrier dataCarrier=messenger.extraMessage();
                dataCarrier.creator().data("onTestIPCChat2运行完毕").create();
                messenger.reply(dataCarrier);
            }
        }.start();

    }
    @IPCChat(url = "test://ipc/chat/ttt_r",priority = 999)
    public String onTestIPCChatWithReturnData(IPCMessenger messenger){
        Log.e("sad-jetpack","------------->onTestIPCChatWithReturnData收到信息:"+messenger.extraMessage().data());

        new Thread(){
            @Override
            public void run() {
                IDataCarrier dataCarrier=messenger.extraMessage();
                dataCarrier.creator().data("onTestIPCChatWithReturnData运行完毕").create();
                messenger.reply(dataCarrier);
            }
        }.start();
        return "ssss";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SCore.registerIPCHost(this);
        String s="";
        AppGo.get().getApplication();
        //华安生态 宝盈互联网 鹏华混合 融通新能源、景气AB 国投新能源 广发新经济
        try {
            /*Map<String, ExposedServiceRelationMappingEntity> map= ExposedServiceManager
                    .newInstance()
                    .repository("xxx://ssss.php.cn/java/")
                    .entityGroup()
                    ;
            Log.e("sad-jetpack","entity列表:"+map);

            IExposedService exposedService= ExposedServiceManager.exposedServiceFirst("xxx://ssss.php.cn/java/base7/index?dww=cs").instance();
            exposedService.action(new IExposedActionNotifier() {
                @Override
                public boolean notifyBy(Object o) {
                    Log.e("sad-jetpack","接收事件模块回调");
                    return false;
                }
            });

            Map<String,Class> m= ExposedServiceManager.newInstance()
                    .repository("xxx://ssss.php.cn/java/")
                    .serviceClassList();
            Log.e("sad-jetpack","Class列表："+m);
            LinkedHashMap<String, Class<ListenableWorker>> mw= ExposedServiceManager.newInstance()
                    .repository("https://www.baidu.com/xxx/")
                    .workerClassGroup();
            Log.e("sad-jetpack","WorkerClass列表："+mw);*/
            testConcurrency();
            //测试
            //scascs
            //scscsc sss
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void test9(){
        try {
            IExposedService exposedService=ExposedServiceManager.newInstance().getFirst("test://s/")
                    .instance();
            exposedService.action(new IPCMessenger() {
                @Override
                public boolean reply(IDataCarrier d, IPCSession session) {
                    Log.e("sad-jetpack","------------->模块回复:"+d);
                    if (session!=null){
                        session.componentChat(DataCarrierImpl.newInstanceCreator().data("干的很好，给你加鸡腿！").create(),this);
                    }
                    return false;
                }

                @Override
                public String messengerId() {
                    return "交给模块的测试工作";
                }

                @Override
                public IDataCarrier extraMessage() {
                    return DataCarrierImpl.newInstanceCreator().data("go go go go").create();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testSequence(){
        SCore.getManager()
                .asyncScanERM()
                .repository("test://group/s/", new IExposedServiceManagerAsync.OnExposedServiceGroupRepositoryFoundListener() {
                    @Override
                    public void onExposedServiceGroupRepositoryFoundSuccess(ICluster cluster) {
                        cluster
                                .call()
                                .proceedMode(PROCEED_MODE_SEQUENCE)
                                .timeout(15)
                                .listener(new IProceedListener() {
                                    @Override
                                    public IDataCarrier onInput(IDataCarrier inputData) {
                                        return inputData;
                                    }

                                    @Override
                                    public void onOutput(IDataCarrier  outputData) {
                                        Log.e("sad-jetpack","------------->串行任务执行完毕:"+outputData.data());
                                    }

                                    @Override
                                    public boolean onProceed(IDataCarrier data, IPCSession session, String messengerProxyId) {
                                        return false;
                                    }

                                    @Override
                                    public void onExceptionInPerformer(Throwable throwable) {
                                        Log.e("sad-jetpack","------------->串行任务执行异常:"+throwable.getMessage());
                                        throwable.printStackTrace();
                                    }
                                })
                                .submit()
                                .start(DataCarrierImpl.newInstanceCreator().data("输入数据").create());
                        ;
                    }

                    @Override
                    public void onExposedServiceGroupRepositoryFoundFailure(Throwable throwable) {

                    }
                });
    }

    private void testConcurrency(){
        SCore.getManager()
                .cluster("test://group/c/")
                .call()
                .proceedMode(PROCEED_MODE_CONCURRENCY)
                .timeout(15)
                .listener(new IProceedListener() {
                    @Override
                    public void onOutput(ConcurrentLinkedHashMap<String,IDataCarrier> outputData) {
                        Log.e("sad-jetpack","------------->并行任务执行完毕:"+outputData);
                    }

                    @Override
                    public void onExceptionInPerformer(Throwable throwable) {
                        Log.e("sad-jetpack","------------->并行任务执行异常:"+throwable.getMessage());
                    }
                })
                .submit()
                .start(DataCarrierImpl.newInstanceCreator().data("并行输入").create());
    }

    private void testPostConcurrency(){
        SCore.getManager()
                .cluster("test://ipc/chat")
                .post()
                .proceedMode(PROCEED_MODE_CONCURRENCY)
                .timeout(9)
                .listener(new IProceedListener() {
                    @Override
                    public boolean onProceed( IDataCarrier data, IPCSession session, String messengerProxyId) {

                        return false;
                    }

                    @Override
                    public void onOutput(ConcurrentLinkedHashMap<String,IDataCarrier> outputData) {
                        Log.e("sad-jetpack","------------->并行任务执行完毕:"+outputData);
                    }

                    @Override
                    public void onExceptionInPerformer(Throwable throwable) {
                        Log.e("sad-jetpack","------------->并行任务执行异常:"+throwable.getMessage());
                    }
                })
                .submit()
                .start(DataCarrierImpl.newInstanceCreator().data("并行输入").create());
    }
    private void testPostSequence(){
        SCore.getManager()
                .cluster("test://ipc/chat")
                .post()
                .proceedMode(PROCEED_MODE_SEQUENCE)
                .timeout(9)
                .listener(new IProceedListener() {
                    @Override
                    public boolean onProceed(IDataCarrier data, IPCSession session, String messengerProxyId) {
                        Log.e("sad-jetpack","------------->路过串行任务:"+messengerProxyId);
                        return false;
                    }

                    @Override
                    public void onOutput(IDataCarrier outputData) {
                        Log.e("sad-jetpack","------------->串行任务执行完毕:"+outputData.data());
                    }

                    @Override
                    public void onExceptionInPerformer(Throwable throwable) {
                        Log.e("sad-jetpack","------------->串行任务执行异常:"+throwable.getMessage());
                    }
                })
                .submit()
                .start(DataCarrierImpl.newInstanceCreator().data("串行输入").create());
    }
    public void testWorker(){
        /*new InternalPerformer(ExposedServiceManager.newInstance()
                .get("https://www.baidu.com/xxx/"))
                .performByWorkerRequest(new IPerformer.IWorkerDispatcher() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onWorkerDispatched(WorkManager manager, LinkedHashMap<String, WorkRequest> requestGroup) {
                        List<OneTimeWorkRequest> requests=new ArrayList<>();
                        for (WorkRequest request :requestGroup.values()) {
                            requests.add((OneTimeWorkRequest) request);
                        }
                        OneTimeWorkRequest result = new OneTimeWorkRequest.Builder(TestWorker3.class)
                                .setConstraints( getDefaultConstraints() )
                                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL,5, TimeUnit.SECONDS)
                                .addTag("end_result")
                                .build()
                                ;
                        manager
                                .beginWith(requests)
                                .then(result)
                                .enqueue()

                        ;
                        manager.getWorkInfoByIdLiveData(result.getId())
                                .observe(MainActivity.this, new Observer<WorkInfo>() {
                                    @Override
                                    public void onChanged(WorkInfo workInfo) {
                                        Log.e("sad-jetpack",">>>>末端任务状态："+workInfo.toString());
                                    }
                                });
                    }
                });*/
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void testWorker2(){
        OneTimeWorkRequest request1 = new OneTimeWorkRequest.Builder(TestWorker.class)
                .setConstraints( getDefaultConstraints() )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL,5, TimeUnit.SECONDS)
                .addTag("t1")//设置任务tag
                .keepResultsForAtLeast(15, TimeUnit.MINUTES)//设置任务的保存时间
                .build();
        OneTimeWorkRequest request2 = new OneTimeWorkRequest.Builder(TestWorker2.class)
                .setConstraints( getDefaultConstraints() )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL,5, TimeUnit.SECONDS)
                .addTag("t2")//设置任务tag
                .keepResultsForAtLeast(15, TimeUnit.MINUTES)//设置任务的保存时间
                .build();
        OneTimeWorkRequest request3 = new OneTimeWorkRequest.Builder(TestWorker3.class)
                .setConstraints( getDefaultConstraints() )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL,5, TimeUnit.SECONDS)
                .addTag("t3")//设置任务tag
                .keepResultsForAtLeast(15, TimeUnit.MINUTES)//设置任务的保存时间
                .build();
        List<OneTimeWorkRequest> list=new ArrayList<>();
        list.add(request1);
        list.add(request2);
        WorkManager workManager=WorkManager.getInstance(getApplicationContext());
        workManager.beginWith(list).then(request3).enqueue()
                /*.getState()
                .observe(MainActivity.this, new Observer<Operation.State>() {
                    @Override
                    public void onChanged(Operation.State state) {
                        Log.e("sad-jetpack",">>>>全部任务执行完毕："+state);
                    }
                })*/
        ;

        workManager.getWorkInfoByIdLiveData(request3.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        Log.e("sad-jetpack",">>>>末端任务状态："+workInfo.toString());
                    }
                });


    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private Constraints getDefaultConstraints(){
        // 设置限定条件
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)  // 网络状态
                .setRequiresBatteryNotLow(false)                 // 可在电量不足时执行
                .setRequiresCharging(false)                      // 可在不充电时执行
                .setRequiresStorageNotLow(false)                 // 可在存储容量不足时执行
                .setRequiresDeviceIdle(false)                    // 在待机状态下执行
                .build();
        return constraints;
    }

    @Override
    protected void onDestroy() {
        SCore.unregisterIPCHost(this);
        super.onDestroy();
    }
}

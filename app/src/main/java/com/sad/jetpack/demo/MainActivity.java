package com.sad.jetpack.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.sad.jetpack.architecture.componentization.annotation.ActivityRouter;
import com.sad.jetpack.architecture.componentization.annotation.Data;
import com.sad.jetpack.architecture.componentization.api.IBody;
import com.sad.jetpack.architecture.componentization.api.BodyImpl;
import com.sad.jetpack.architecture.componentization.api.IComponentProcessorCallListener;
import com.sad.jetpack.architecture.componentization.api.IResponse;
import com.sad.jetpack.architecture.componentization.api.LogcatUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sad.jetpack.architecture.componentization.annotation.IPCChat;
import com.sad.jetpack.architecture.componentization.api.IRequest;
import com.sad.jetpack.architecture.componentization.api.IResponseSession;
import com.sad.jetpack.architecture.componentization.api.InstancesRepository;
import com.sad.jetpack.architecture.componentization.api.ParasiticComponentRepositoryFactory;
import com.sad.jetpack.architecture.componentization.api.RequestImpl;
import com.sad.jetpack.architecture.componentization.api.SCore;
import com.sad.jetpack.architecture.componentization.api.Utils;
import com.sad.jetpack.architecture.componentization.api.extension.router.ActivityLauncherMaster;

public class MainActivity extends AppCompatActivity {
    @IPCChat(url = {"test://ipc/chat/ttt","test://ipc/chat/sss"},priority = {996,822})
    public void onTestIPCChat(IRequest request, String xxx, IResponseSession session,@Data(name = "extra")Bundle bundle){
        LogcatUtils.e("sad-jetpack","------------->onTestIPCChat收到信息:"+request.body().dataContainer().getMap());
        tv.setText("onTestIPCChat收到信息:"+request.body().dataContainer().getMap());
        new Thread(){
            @Override
            public void run() {
                request.body().dataContainer().put("xxx","89898989");
                session.postResponseData(request.body());
            }
        }.start();
    }
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=findViewById(R.id.myProcess);
        tv.setText("当前App:"+getApplicationContext().getPackageName()+"\n当前进程:"+ Utils.getCurrAppProccessName(getApplicationContext()));
        //华安生态 宝盈互联网 鹏华混合 融通新能源、景气AB 国投新能源 广发新经济
        SCore.registerParasiticComponentFromHost(this);
        findViewById(R.id.shap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this,RemoteActivity.class));
                //ActivityLauncherMaster.newInstance(MainActivity.this)
                //        .start("activity://demo.v1");
                testCall();
            }
        });
    }

    private void testCall(){
        //SCore.getComponentCallable(getApplicationContext(),"xxx://ssss.php.cn/java").call(RequestImpl.newBuilder("551").build());
        SCore.asSequenceProcessor("xxx")
                .listener(new IComponentProcessorCallListener() {
                    @Override
                    public boolean onProcessorReceivedResponse(IResponse response, String processorId) {
                        LogcatUtils.e(">>>回调完毕："+response);
                        return false;
                    }

                    @Override
                    public IResponse onProcessorMergeResponses(ConcurrentLinkedHashMap<IResponse, String> responses, String processorId) {
                        return null;
                    }

                    @Override
                    public void onProcessorException(IRequest request, Throwable throwable, String processorId) {

                    }
                })
                .build()
                .join(SCore.getCluster(getApplicationContext()).repository("test://tsc/").componentCallableInstances())
                .submit(RequestImpl.newBuilder("666").body(BodyImpl.newBuilder().extraObject("xxxxxx").build()).build());
        ;
    }

    private void testPostMsgRemote(){

    }


    private void testPostMsgToLocal(){
        try {
            IBody body= BodyImpl.newBuilder()
                    .addData("xxx","666666")
                    .build();
            IRequest request= RequestImpl.newBuilder("c")
                    .body(body)
                    .build()
                    ;
            InstancesRepository instancesRepository=SCore.getCluster(getApplicationContext())
                    .instancesRepositoryFactory(ParasiticComponentRepositoryFactory.newInstance())
                    .repository("test://ipc/chat")
                    ;
            SCore
                    .asSequenceProcessor("5")
                    .build()
                    .join(instancesRepository.componentCallableInstances())
                    .submit(request);
        }catch (Exception e){
            e.printStackTrace();
        }
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
                                        LogcatUtils.e("sad-jetpack",">>>>末端任务状态："+workInfo.toString());
                                    }
                                });
                    }
                });*/
    }

    /*@RequiresApi(api = Build.VERSION_CODES.M)
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
                *//*.getState()
                .observe(MainActivity.this, new Observer<Operation.State>() {
                    @Override
                    public void onChanged(Operation.State state) {
                        LogcatUtils.e("sad-jetpack",">>>>全部任务执行完毕："+state);
                    }
                })*//*
        ;

        workManager.getWorkInfoByIdLiveData(request3.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        LogcatUtils.e("sad-jetpack",">>>>末端任务状态："+workInfo.toString());
                    }
                });


    }*/
    /*@RequiresApi(api = Build.VERSION_CODES.M)
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
    }*/
}

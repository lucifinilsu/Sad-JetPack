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
import androidx.work.WorkRequest;

import com.sad.jetpack.architecture.appgo.api.AppGo;
import com.sad.jetpack.architecture.componentization.api.ExposedServiceManager;
import com.sad.jetpack.architecture.componentization.api.IPCMessenger;
import com.sad.jetpack.architecture.componentization.api.IPCSession;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IPerformer;
import com.sad.jetpack.architecture.componentization.api.InternalPerformer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            test9();
            //测试
            //scascs
            //scscsc sss
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void test9(){
        try {
            IExposedService exposedService=ExposedServiceManager.getFirst("xxx://ssss.php.cn/java/base7/")
                    .instance();
            exposedService.action(new IPCMessenger() {
                @Override
                public boolean reply(Object d,IPCSession session) {
                    Log.e("sad-jetpack","------------->模块回复:"+d);
                    if (session!=null){
                        session.componentChat("干的很好，给你加鸡腿！",this);
                    }
                    return false;
                }

                @Override
                public String messengerId() {
                    return "交给模块的测试工作";
                }

                @Override
                public String extraMessage() {
                    return "gogogo";
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void testWorker(){
        new InternalPerformer(ExposedServiceManager.newInstance()
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
                });
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

}

package com.sad.jetpack.demo;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkContinuation;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.sad.jetpack.architecture.appgo.api.AppGo;
import com.sad.jetpack.architecture.componentization.api.ExposedServiceRelationMappingEntity;
import com.sad.jetpack.architecture.componentization.api.ExposedServiceManager;
import com.sad.jetpack.architecture.componentization.api.IExposedActionNotifier;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IPerformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String s="";
        AppGo.get().getApplication();

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
            testWorker();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void testWorker(){
        ExposedServiceManager.newInstance()
                .repository("https://www.baidu.com/xxx/")
                .commit()
                .perform(new IPerformer.IWorkerDispatcher() {
                    @Override
                    public void onWorkerDispatched(WorkManager manager, LinkedHashMap<String, WorkRequest> requestGroup) {
                        List<OneTimeWorkRequest> requests=new ArrayList<>();
                        for (WorkRequest request :requestGroup.values()) {
                            requests.add((OneTimeWorkRequest) request);
                        }
                        manager
                                .beginWith(requests)
                                .enqueue()
                                .getState()
                                .observe(MainActivity.this, new Observer<Operation.State>() {
                                    @Override
                                    public void onChanged(Operation.State state) {
                                        Log.e("sad-jetpack",">>>>全部任务执行完毕："+state);
                                    }
                                });
                        ;
                    }
                });
    }

}

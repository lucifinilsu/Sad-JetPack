package com.sad.jetpack.demo;

import android.util.Log;

import androidx.work.Data;
import androidx.work.ListenableWorker;

import com.sad.jetpack.architecture.componentization.annotation.ExposedService;
import com.sad.jetpack.architecture.componentization.api.IExposedActionNotifier;
import com.sad.jetpack.architecture.componentization.api.IExposedWorkerService;

@ExposedService(url = "https://www.baidu.com/xxx/45/cc/gg",asyncWorker = true)
public class TestWorkerAsyncService implements IExposedWorkerService {
    @Override
    public ListenableWorker.Result actionForWorker(IExposedActionNotifier<ListenableWorker.Result> notifier, ListenableWorker worker) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                Data data=new Data.Builder()
                        .putString("data1","a")
                        .build();
                Log.e("sad-jetpack",">>>>异步任务1执行完毕");
                notifier.notifyBy(ListenableWorker.Result.success(data));
            }
        }.start();

        return null;
    }
}

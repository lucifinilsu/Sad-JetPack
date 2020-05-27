package com.sad.jetpack.demo;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.work.Data;
import androidx.work.ListenableWorker;

import com.sad.jetpack.architecture.componentization.annotation.ExposedService;
import com.sad.jetpack.architecture.componentization.api.IExposedActionNotifier;
import com.sad.jetpack.architecture.componentization.api.IExposedWorkerService;

@ExposedService(url = "https://www.baidu.com/xxx/45/cc/222",asyncWorker = true)
public class TestWorkerAsyncService2 implements IExposedWorkerService {
    @SuppressLint("RestrictedApi")
    @Override
    public ListenableWorker.Result actionForWorker(IExposedActionNotifier<ListenableWorker.Result> notifier, ListenableWorker worker) {
        /*new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Data data=new Data.Builder()
                        .putString("data1","a")
                        .build();
                Log.e("sad-jetpack",">>>>异步任务2执行完毕");
                notifier.notifyBy(ListenableWorker.Result.success(data));
            }
        }.start();*/

        worker.getBackgroundExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Data data=new Data.Builder()
                        .putString("data1","a")
                        .build();
                Log.e("sad-jetpack",">>>>异步任务2执行完毕");
                notifier.notifyBy(ListenableWorker.Result.success(data));
            }
        });

        return null;
    }
}

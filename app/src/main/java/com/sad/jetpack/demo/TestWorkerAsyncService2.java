package com.sad.jetpack.demo;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.work.Data;
import androidx.work.ListenableWorker;

import com.sad.jetpack.architecture.componentization.annotation.ExposedService;
import com.sad.jetpack.architecture.componentization.api.DataState;
import com.sad.jetpack.architecture.componentization.api.IPCMessenger;
import com.sad.jetpack.architecture.componentization.api.IExposedWorkerService;
import com.sad.jetpack.architecture.componentization.api.impl.DataCarrierImpl;

@ExposedService(url = {"https://www.baidu.com/xxx/45/cc/222","https://www.baidu.com/yyy/ooo","https://www.baidu.com/yyy/oo/cww"},asyncWorker = true)
public class TestWorkerAsyncService2 implements IExposedWorkerService {
    @SuppressLint("RestrictedApi")
    @Override
    public ListenableWorker.Result actionForWorker(IPCMessenger messenger) {
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
        ListenableWorker worker=messenger.extraMessage().data();
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

                messenger.reply(DataCarrierImpl.newInstanceCreator().data(ListenableWorker.Result.success(data)).state(DataState.DONE).create());
            }
        });

        return null;
    }
}

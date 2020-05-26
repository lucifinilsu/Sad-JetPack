package com.sad.jetpack.demo;

import android.util.Log;

import androidx.work.Data;
import androidx.work.ListenableWorker;

import com.sad.jetpack.architecture.componentization.annotation.ExposedService;
import com.sad.jetpack.architecture.componentization.api.IExposedActionNotifier;
import com.sad.jetpack.architecture.componentization.api.IExposedWorkerService;
@ExposedService(url = "https://www.baidu.com/xxx/45/cc/wfw")
public class TestWorkerService implements IExposedWorkerService {
    @Override
    public ListenableWorker.Result actionForWorker(IExposedActionNotifier<ListenableWorker.Result> notifier, ListenableWorker worker) {
        Data data=new Data.Builder()
                .putString("data2","b")
                .build();
        Log.e("sad-jetpack",">>>>同步任务执行完毕");
        return ListenableWorker.Result.success(data);
    }
}

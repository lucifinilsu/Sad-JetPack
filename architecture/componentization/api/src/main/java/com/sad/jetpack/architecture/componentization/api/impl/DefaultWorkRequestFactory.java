package com.sad.jetpack.architecture.componentization.api.impl;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkRequest;

import com.sad.jetpack.architecture.componentization.api.IPerformer;

import java.util.concurrent.TimeUnit;

public class DefaultWorkRequestFactory implements IPerformer.IWorkerRequestFactory {
    @Override
    public WorkRequest workerRequest(String ermPath,Class<ListenableWorker> workerClass, Constraints defaultConstraints) {
        //单次任务
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(workerClass)
                .setConstraints( defaultConstraints )
                //设置任务的重试策略，比如我们在Worker类的doWork()函数返回Result.RETRY，让该任务又重新入队。
                //BackoffPolicy.LINEAR 线性增加 比如：第一次是2s 那么第二次就是4s
                //BackoffPolicy.EXPONENTIAL 指数增加
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL,5, TimeUnit.SECONDS)
                .addTag(ermPath)//设置任务tag
                .keepResultsForAtLeast(15, TimeUnit.MINUTES)//设置任务的保存时间
                //.setInputData()//设置任务的传入参数
                .build();
        //定时任务   时间不能低于15分钟
        /*PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(MyWorker.class,15,TimeUnit.MINUTES)
                .setConstraints( constraints  )
                .build();*/
        return request;
    }
}

package com.sad.jetpack.architecture.componentization.api.task;

public interface ITask {

    void proceed(ITaskChain chain, ITaskResultNotifier notifier);

    default String taskName(){return "";};

    String taskId();


}

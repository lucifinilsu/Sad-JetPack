package com.sad.jetpack.architecture.componentization.api.task;

public interface ITaskResultNotifier {

    ITaskResultNotifier result(ITaskResult result);

    void notifyChanged();

}

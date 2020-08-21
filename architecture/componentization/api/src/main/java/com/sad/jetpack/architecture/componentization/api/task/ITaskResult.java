package com.sad.jetpack.architecture.componentization.api.task;

import androidx.annotation.NonNull;

import com.sad.jetpack.security.ISecurity;

public interface ITaskResult {

    <T> T data();

    TaskState state();

    Creator creator(@NonNull ISecurity security) throws Exception;

    interface Creator{

        Creator data(Object o);

        Creator state(TaskState state);

        ITaskResult create();
    }
}

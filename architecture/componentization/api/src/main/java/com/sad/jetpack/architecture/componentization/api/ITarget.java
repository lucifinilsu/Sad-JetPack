package com.sad.jetpack.architecture.componentization.api;

import android.os.Parcelable;

public interface ITarget extends Parcelable {

    String id();

    String toApp();

    String toProcess();

    @ProcessorMode int processorMode();

    Builder toBuilder();

    interface Builder{

        Builder id(String id);

        Builder toApp(String toApp);

        Builder toProcess(String toProcess);

        Builder processorMode(@ProcessorMode int processorMode);

        ITarget build();
    }
}

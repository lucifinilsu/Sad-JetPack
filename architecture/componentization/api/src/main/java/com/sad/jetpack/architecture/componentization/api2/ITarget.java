package com.sad.jetpack.architecture.componentization.api2;

import android.os.Parcelable;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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

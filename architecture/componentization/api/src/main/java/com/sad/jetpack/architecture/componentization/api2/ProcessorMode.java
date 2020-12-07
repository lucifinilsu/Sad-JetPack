package com.sad.jetpack.architecture.componentization.api2;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.sad.jetpack.architecture.componentization.api2.ProcessorMode.*;


@IntDef({PROCESSOR_MODE_SEQUENCE, PROCESSOR_MODE_CONCURRENCY,PROCESSOR_MODE_SINGLE})
@Retention(RetentionPolicy.SOURCE)
public @interface ProcessorMode {

    int PROCESSOR_MODE_SEQUENCE=0;

    int PROCESSOR_MODE_CONCURRENCY=1;

    int PROCESSOR_MODE_SINGLE=-1;
}
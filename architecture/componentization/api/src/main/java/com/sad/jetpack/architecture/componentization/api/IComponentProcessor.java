package com.sad.jetpack.architecture.componentization.api;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface IComponentProcessor<I extends IComponentProcessor<I>> {
    int PROCEED_MODE_CONCURRENCY =1;
    int PROCEED_MODE_SEQUENCE =2;
    int PROCEED_MODE_CUSTOMER =4;
    @IntDef({
            PROCEED_MODE_CONCURRENCY,
            PROCEED_MODE_SEQUENCE,
            PROCEED_MODE_CUSTOMER
    })
    @Target({
            ElementType.PARAMETER,
            ElementType.FIELD,
    }) //表示注解作用范围，参数注解，成员注解，方法注解
    @Retention(RetentionPolicy.SOURCE) //表示注解所存活的时间,在运行时,而不会存在 .class 文件中
    @interface ProceedMode {}

    I timeout(long timeout);

    IPerformer submit();

    I listener(IProceedListener callerListener);

    I proceedMode(@ProceedMode int processMode);
}

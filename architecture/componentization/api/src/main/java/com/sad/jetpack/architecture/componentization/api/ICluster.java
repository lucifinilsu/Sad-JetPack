package com.sad.jetpack.architecture.componentization.api;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface ICluster {

    int CALL_MODE_CONCURRENCY=1;
    int CALL_MODE_SEQUENCE=2;
    int CALL_MODE_CUSTOMER=4;
    @IntDef({
            CALL_MODE_CONCURRENCY,
            CALL_MODE_SEQUENCE,
            CALL_MODE_CUSTOMER
            })
    @Target({
            ElementType.PARAMETER,
            ElementType.FIELD,
    }) //表示注解作用范围，参数注解，成员注解，方法注解
    @Retention(RetentionPolicy.SOURCE) //表示注解所存活的时间,在运行时,而不会存在 .class 文件中
    @interface CallMode{}

    IExposedServiceGroupRepository repository();

    ICluster addInstanceConstructorParameters(@NonNull String e_url, @NonNull IExposedServiceInstanceConstructorParameters parameters);

    ICluster addInstanceConstructorParameters(@NonNull IExposedServiceInstanceConstructorParameters parameters);

    ICluster exclude(String... e_url);

    ICluster addExtraExposedServiceInstance(IExposedService exposedService);

    IProcessor call();

    IProcessor post();

    ICluster processMode(@CallMode int processMode);

}

package com.sad.jetpack.architecture.componentization.api;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface ICluster {



    IExposedServiceGroupRepository repository();

    ICluster addInstanceConstructorParameters(@NonNull String e_url, @NonNull IExposedServiceInstanceConstructorParameters parameters);

    ICluster addInstanceConstructorParameters(@NonNull IExposedServiceInstanceConstructorParameters parameters);

    ICluster exclude(String... e_url);

    ICluster addExtraExposedServiceInstance(IExposedService exposedService,String orgUrl);

    IProcessor call();

    IProcessor post();

    IProcessor proceedAs(IExposedServiceInstancesFactory factory);

}

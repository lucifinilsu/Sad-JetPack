package com.sad.jetpack.architecture.componentization.api;

import androidx.annotation.NonNull;

public interface ICluster {

    IExposedServiceGroupRepository repository();

    ICluster addInstanceConstructorParameters(@NonNull String e_url, @NonNull IExposedServiceInstanceConstructorParameters parameters);

    ICluster addInstanceConstructorParameters(@NonNull IExposedServiceInstanceConstructorParameters parameters);

    ICluster exclude(String... e_url);

    ICluster addExtraExposedServiceInstance(IExposedService exposedService,String orgUrl);

    ILocalProcessor call();

    ILocalProcessor post();

    ILocalProcessor proceedAs(IExposedServiceInstancesFactory factory);

    IRemoteProcessor post(String processName);

    IRemoteProcessor post(String appPkg, String processName);

}

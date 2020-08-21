package com.sad.jetpack.architecture.componentization.api;

public interface IExposedServiceManagerAsync{

    IExposedServiceManagerAsync entityGroupFactory(IExposedServiceEntityGroupFactory entityGroupFactory);

    IExposedServiceManagerAsync entityFoundListener(IExposedServiceEntityGroupFactory.OnExposedServiceRelationMappingEntityFoundListener onExposedServiceRelationMappingEntityFoundListener);

    void repository(String url,OnExposedServiceGroupRepositoryFoundListener repositoryFoundListener);

    interface OnExposedServiceGroupRepositoryFoundListener{

        void onExposedServiceGroupRepositoryFoundSuccess(ICluster cluster);

        void onExposedServiceGroupRepositoryFoundFailure(Throwable throwable);
    }

}

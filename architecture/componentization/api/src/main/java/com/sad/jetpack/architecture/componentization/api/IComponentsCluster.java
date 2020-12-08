package com.sad.jetpack.architecture.componentization.api;



import java.util.Map;
import java.util.concurrent.Future;

public interface IComponentsCluster {

    IComponentsCluster addConstructorToAll(IConstructor constructor);

    IComponentsCluster addConstructor(String curl,IConstructor constructor);

    IComponentsCluster addConstructors(Map<String, IConstructor> constructors);

    IComponentsCluster instanceInitializeListener(IComponentCallableInitializeListener listener);

    IComponentsCluster instancesRepositoryFactory(InstancesRepositoryFactory instancesRepositoryFactory);

    InstancesRepository repository(String url);

    Future<InstancesRepository> repositoryAsync(String url,IComponentRepositoryObtainedCallback callback);

    interface IComponentRepositoryObtainedCallback {
        void onInstancesRepositoryObtained(InstancesRepository repository);
    }
}

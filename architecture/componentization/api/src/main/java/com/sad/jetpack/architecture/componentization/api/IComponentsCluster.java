package com.sad.jetpack.architecture.componentization.api;

import java.util.Map;
import java.util.concurrent.Future;

public interface IComponentsCluster {

    IComponentsCluster addConstructorToAll(IConstructor constructor);

    IComponentsCluster addConstructor(String curl,IConstructor constructor);

    IComponentsCluster addConstructors(Map<String,IConstructor> constructors);

    IComponentsCluster instanceInitializeListener(IComponentInitializeListener listener);

    IComponentsCluster componentRepositoryFactory(IComponentRepositoryFactory componentRepositoryFactory);

    IComponentRepository repository(String url);

    Future<IComponentRepository> repositoryAsync(String url,IComponentRepositoryObtainedCallback callback);

    interface IComponentRepositoryObtainedCallback {
        void onComponentRepositoryObtained(IComponentRepository repository);
    }
}

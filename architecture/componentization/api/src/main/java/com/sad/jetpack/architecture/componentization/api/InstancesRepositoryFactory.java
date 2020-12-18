package com.sad.jetpack.architecture.componentization.api;


import android.content.Context;
import android.os.Parcelable;

import java.util.Map;

public interface InstancesRepositoryFactory extends Parcelable {

    InstancesRepository from(
            Context context,
            String url,
            IConstructor allConstructor,
            Map<String, IConstructor> constructors,
            IComponentCallableInitializeListener listener
    );

}

package com.sad.jetpack.architecture.componentization.api;

import android.os.Messenger;

public interface IComponent {

    void onCall(IExposedServiceGroupRepository serviceGroupRepository,IComponentRequest request);

}

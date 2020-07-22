package com.sad.jetpack.architecture.componentization.api;


public interface IComponent {

    void onCall(IExposedServiceGroupRepository serviceGroupRepository,IComponentRequest request);

}

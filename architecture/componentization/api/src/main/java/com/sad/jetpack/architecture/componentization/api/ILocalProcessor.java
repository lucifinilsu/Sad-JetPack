package com.sad.jetpack.architecture.componentization.api;
import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.LinkedHashMap;

public interface ILocalProcessor<I extends ILocalProcessor<I>> extends IProcessor<I> {

    LinkedHashMap<Object,String> extraObjectInstances();

    LinkedHashMap<IExposedService,String> exposedServiceInstance();

}

package com.sad.jetpack.architecture.componentization.annotation;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Deprecated
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExposedService {

    String[] assetsDir() default {"src\\main\\assets\\"};

    String[] url();

    String description() default "";

    boolean asyncWorker() default false;
}

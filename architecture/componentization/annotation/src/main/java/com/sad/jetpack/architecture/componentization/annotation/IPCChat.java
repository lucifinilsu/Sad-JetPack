package com.sad.jetpack.architecture.componentization.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IPCChat {

    String[] assetsDir() default {"src\\main\\assets\\"};

    String[] url();

    /*String parametersName() default "";*/

    String description() default "";

    int[] priority() default {0};

    int version() default 0;

}

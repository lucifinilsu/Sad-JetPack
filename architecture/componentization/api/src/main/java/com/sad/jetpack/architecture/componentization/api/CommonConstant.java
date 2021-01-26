package com.sad.jetpack.architecture.componentization.api;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CommonConstant {

    public final static String REMOTE_BUNDLE_REQUEST="REMOTE_BUNDLE_REQUEST";

    public final static String REMOTE_BUNDLE_RESPONSE="REMOTE_BUNDLE_RESPONSE";

    public final static String REMOTE_BUNDLE_TARGET="REMOTE_BUNDLE_TARGET";

    public static final String REMOTE_BUNDLE_ACTION = "REMOTE_BUNDLE_ACTION";

    public static final String REMOTE_BUNDLE_THROWABLE = "REMOTE_BUNDLE_THROWABLE";

    public static final String REMOTE_BUNDLE_CALLER_CONFIG = "REMOTE_BUNDLE_CALLER_CONFIG";

    public static final String REMOTE_BUNDLE_CALLER_INSTANCES_REPOSITORY_FACTORY = "REMOTE_BUNDLE_CALLER_INSTANCES_REPOSITORY_FACTORY";

    protected static boolean enableLogUtils =false;

    protected static boolean enableInternalLog=false;

    protected static boolean wholeLog=false;
}

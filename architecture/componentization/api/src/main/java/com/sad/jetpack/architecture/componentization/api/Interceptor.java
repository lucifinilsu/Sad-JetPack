package com.sad.jetpack.architecture.componentization.api;

import androidx.annotation.NonNull;

import java.io.Serializable;
@Deprecated
public interface Interceptor extends Comparable<Interceptor>, Serializable {


    void onRequestIntercepted();

    default public int interceptorPriority(){
        return 0;
    };
    @Override
    default int compareTo(@NonNull Interceptor o){return o.interceptorPriority()-this.interceptorPriority();};
}

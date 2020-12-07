package com.sad.jetpack.architecture.componentization.api;

public class DefaultSequenceMessageBoundaryInterceptor implements ISequenceMessageBoundaryInterceptor {
    private DefaultSequenceMessageBoundaryInterceptor(){}
    public static ISequenceMessageBoundaryInterceptor getInstance(){
        return new DefaultSequenceMessageBoundaryInterceptor();
    }
}

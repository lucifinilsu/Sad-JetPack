package com.sad.jetpack.architecture.componentization.api2;

public class DefaultSequenceMessageBoundaryInterceptor implements ISequenceMessageBoundaryInterceptor {
    private DefaultSequenceMessageBoundaryInterceptor(){}
    public static ISequenceMessageBoundaryInterceptor getInstance(){
        return new DefaultSequenceMessageBoundaryInterceptor();
    }
}

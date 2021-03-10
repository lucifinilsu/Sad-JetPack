package com.sad.jetpack.architecture.componentization.api;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class RequestImpl implements IRequest, IRequest.Builder, Parcelable {
    private String id;
    private String fromApp;
    private String fromProcess;
    private IBody body;
    private IResponse previousResponse;


    public static final Creator<IRequest> CREATOR = new Creator<IRequest>() {
        @Override
        public IRequest createFromParcel(Parcel in) {
            IRequest request= new RequestImpl();
            request.readFromParcel(in);
            return request;
        }

        @Override
        public IRequest[] newArray(int size) {
            return new RequestImpl[size];
        }
    };

    public static IRequest newInstance(String id){
        return new RequestImpl(id);
    }

    public static IRequest.Builder newBuilder(String id){
        return new RequestImpl(id);
    }

    protected RequestImpl(){
        previousResponse(ResponseImpl.newBuilder().build());
    }

    private RequestImpl(String id){
        this();
        this.id=id;
        Context context=InternalContextHolder.get().getContext();
        if (context!=null){
            this.fromApp=context.getPackageName();
            this.fromProcess=Utils.getCurrAppProccessName(context);
        }
    }




    public String id() {
        return id;
    }

    @Override
    public IResponse previousResponse() {
        return this.previousResponse;
    }

    @Override
    public Builder toBuilder() {
        return this;
    }


    public String fromApp() {
        return fromApp;
    }

    public String fromProcess() {
        return fromProcess;
    }

    @Override
    public IBody body() {
        return this.body;
    }


    public RequestImpl fromApp(String app){
        this.fromApp=app;
        return this;
    }

    public RequestImpl fromProcess(String process){
        this.fromProcess=process;
        return this;
    }

    @Override
    public Builder body(IBody body) {
        this.body=body;
        return this;
    }

    @Override
    public Builder id(String id) {
        this.id=id;
        return this;
    }

    @Override
    public Builder previousResponse(IResponse response) {
        this.previousResponse=response;
        return this;
    }


    @Override
    public RequestImpl build() {
        return this;
    }


}

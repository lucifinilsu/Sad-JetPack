package com.sad.jetpack.architecture.componentization.api;

import android.os.Parcel;

public class ResponseImpl implements IResponse, IResponse.Builder {

    private IRequest request;
    private IBody body;

    public static final Creator<IResponse> CREATOR = new Creator<IResponse>() {
        @Override
        public IResponse createFromParcel(Parcel in) {
            IResponse response= new ResponseImpl();
            response.readFromParcel(in);
            return response;
        }

        @Override
        public IResponse[] newArray(int size) {
            return new ResponseImpl[size];
        }
    };

    public static IResponse newInstance(){
        return new ResponseImpl();
    }
    public static IResponse.Builder newBuilder(){
        return new ResponseImpl();
    }
    protected ResponseImpl() {

    }


    @Override
    public IRequest request() {
        return this.request;
    }

    @Override
    public IBody body() {
        return this.body;
    }


    @Override
    public Builder toBuilder() {
        return this;
    }

    @Override
    public Builder request(IRequest request) {
        this.request=request;
        return this;
    }

    @Override
    public Builder body(IBody body) {
        this.body=body;
        return this;
    }

    @Override
    public ResponseImpl build() {
        return this;
    }
}

package com.sad.jetpack.architecture.componentization.api;

import android.os.Parcel;

import java.util.Map;

public class BodyImpl implements IBody,IBody.Builder{
    private IDataContainer dataContainer=DefaultDataContainer.newIntance();
    private int what=-1;
    private String description="";
    private Object extraObject;

    protected BodyImpl(){}
    public static IBody newInstance(){
        return new BodyImpl();
    }
    public static IBody.Builder newBuilder(){
        return new BodyImpl();
    }
    public static final Creator<IBody> CREATOR = new Creator<IBody>() {
        @Override
        public IBody createFromParcel(Parcel in) {
            IBody body= new BodyImpl();
            body.readFromParcel(in);
            return body;
        }

        @Override
        public IBody[] newArray(int size) {
            return new BodyImpl[size];
        }
    };

    @Override
    public IDataContainer dataContainer() {
        return this.dataContainer;
    }

    @Override
    public <T> T extraObject() {
        return (T) this.extraObject;
    }

    @Override
    public int what() {
        return this.what;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public Builder toBuilder() {
        return this;
    }

    @Override
    public Builder dataContainer(IDataContainer dataContainer) {
        this.dataContainer=dataContainer;
        return this;
    }

    @Override
    public Builder what(int what) {
        this.what=what;
        return this;
    }

    @Override
    public Builder extraObject(Object extra) {
        this.extraObject=extra;
        return this;
    }

    @Override
    public Builder description(String des) {
        this.description=des;
        return this;
    }

    public Builder addData(String name, Object data){
        if (dataContainer !=null){
            dataContainer.put(name,data);
        }
        return this;
    }
    public Builder addData(Map data){
        if (dataContainer !=null){
            dataContainer.putAll(data);
        }
        return this;
    }
    public Builder addData(IDataContainer data){
        if (dataContainer !=null && data!=null){
            dataContainer.putAll(data);
        }
        return this;
    }

    @Override
    public IBody build() {
        return this;
    }
}

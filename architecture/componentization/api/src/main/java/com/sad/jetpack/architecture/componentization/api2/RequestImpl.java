package com.sad.jetpack.architecture.componentization.api2;
import android.content.Context;
import android.os.Parcel;

import java.util.Map;

public class RequestImpl implements IRequest, IRequest.Builder {
    private String id;
    private String fromApp;
    private String fromProcess;
    private IDataContainer dataContainer =DefaultDataContainer.newIntance();

    public static IRequest newInstance(String id){
        return new RequestImpl(id);
    }

    public static IRequest.Builder newBuilder(String id){
        return new RequestImpl(id);
    }

    private RequestImpl(String id){
        this.id=id;
        Context context=InternalContextHolder.get().getContext();
        this.fromApp=context.getPackageName();
        this.fromProcess=Utils.getCurrAppProccessName(context);
    }

    protected RequestImpl(Parcel in) {
        id = in.readString();
        fromApp = in.readString();
        fromProcess = in.readString();
        dataContainer= (IDataContainer) in.readSerializable();
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public RequestImpl createFromParcel(Parcel in) {
            return new RequestImpl(in);
        }

        @Override
        public RequestImpl[] newArray(int size) {
            return new RequestImpl[size];
        }
    };

    public String id() {
        return id;
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
    public IDataContainer dataContainer() {
        return this.dataContainer;
    }

    public RequestImpl fromApp(String app){
        this.fromApp=app;
        return this;
    }

    public RequestImpl fromProcess(String process){
        this.fromProcess=process;
        return this;
    }

    public RequestImpl dataContainer(IDataContainer dataContainer){
        this.dataContainer =dataContainer;
        return this;
    }
    public RequestImpl addData(String name, Object data){
        if (dataContainer !=null){
            dataContainer.getMap().put(name,data);
        }
        return this;
    }
    public RequestImpl addData(Map data){
        if (dataContainer !=null){
            dataContainer.getMap().putAll(data);
        }
        return this;
    }
    public RequestImpl addData(IDataContainer data){
        if (dataContainer !=null && data!=null){
            dataContainer.getMap().putAll(data.getMap());
        }
        return this;
    }

    @Override
    public RequestImpl build() {
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(fromApp);
        dest.writeString(fromProcess);
        dest.writeSerializable(dataContainer);
    }
}

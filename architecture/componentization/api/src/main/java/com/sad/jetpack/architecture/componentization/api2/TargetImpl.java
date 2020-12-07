package com.sad.jetpack.architecture.componentization.api2;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class TargetImpl implements ITarget,ITarget.Builder {
    private String url="";
    private String toApp="";
    private String toProcess="";
    private @ProcessorMode int processorMode=ProcessorMode.PROCESSOR_MODE_CONCURRENCY;
    private TargetImpl(){}

    protected TargetImpl(Parcel in) {
        url = in.readString();
        toApp = in.readString();
        toProcess = in.readString();
        processorMode = in.readInt();
    }

    public static final Creator<TargetImpl> CREATOR = new Creator<TargetImpl>() {
        @Override
        public TargetImpl createFromParcel(Parcel in) {
            return new TargetImpl(in);
        }

        @Override
        public TargetImpl[] newArray(int size) {
            return new TargetImpl[size];
        }
    };

    public static ITarget newInstance(){
        return new TargetImpl();
    }
    public static ITarget.Builder newBuilder(){
        return new TargetImpl();
    }

    @Override
    public String id() {
        return this.url;
    }

    @Override
    public String toApp() {
        return this.toApp;
    }

    @Override
    public String toProcess() {
        return this.toProcess;
    }

    @Override
    public @ProcessorMode int processorMode() {
        return this.processorMode;
    }

    @Override
    public Builder toBuilder() {
        return this;
    }

    @Override
    public Builder id(String id) {
        this.url= id;
        return this;
    }

    @Override
    public Builder toApp(String toApp) {
        this.toApp=toApp;
        return this;
    }

    @Override
    public Builder toProcess(String toProcess) {
        this.toProcess=toProcess;
        return this;
    }

    @Override
    public Builder processorMode(@ProcessorMode int processorMode) {
        this.processorMode=processorMode;
        return this;
    }

    @Override
    public TargetImpl build() {
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(toApp);
        dest.writeString(toProcess);
        dest.writeInt(processorMode);
    }
}

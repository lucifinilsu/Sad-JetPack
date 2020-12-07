package com.sad.jetpack.architecture.componentization.api;

import android.content.Context;
import android.os.Parcel;

public class IPCTargetImpl implements IPCTarget {
    protected IPCTargetImpl(Parcel in) {
        toApp = in.readString();
        toProcess = in.readString();
        url = in.readString();
        processorMode=in.readInt();
        delay=in.readLong();
        timeout=in.readLong();
    }

    public static final Creator<IPCTargetImpl> CREATOR = new Creator<IPCTargetImpl>() {
        @Override
        public IPCTargetImpl createFromParcel(Parcel in) {
            return new IPCTargetImpl(in);
        }

        @Override
        public IPCTargetImpl[] newArray(int size) {
            return new IPCTargetImpl[size];
        }
    };

    public static IPCTargetImpl newInstance(){
        return new IPCTargetImpl();
    }
    public static IPCTargetImpl toProcess(Context context){
        return new IPCTargetImpl().toApp(context.getPackageName()).toProcess(Utils.getCurrAppProccessName(context));
    }
    public static IPCTargetImpl toCurrProcess(){
        return toProcess(InternalContextHolder.get().getContext());
    }
    private String toApp="";
    private String toProcess="";
    private String url="";
    private int processorMode=PROCESSOR_MODE_SEQUENCE;
    private long delay=-1;
    private long timeout=-1;
    private IPCTargetImpl(){}

    public IPCTargetImpl toApp(String toApp){
        this.toApp=toApp;
        return this;
    }
    public IPCTargetImpl toProcess(String toProcess){
        this.toProcess=toProcess;
        return this;
    }

    public IPCTargetImpl url(String url){
        this.url=url;
        return this;
    }
    public IPCTargetImpl processorMode(int processorMode){
        this.processorMode=processorMode;
        return this;
    }
    public IPCTargetImpl delay(long delay){
        this.delay=delay;
        return this;
    }

    public IPCTargetImpl timeout(long timeout){
        this.timeout=timeout;
        return this;
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
    public String url() {
        return this.url;
    }

    @Override
    public int prcessorMode() {
        return this.processorMode;
    }

    @Override
    public long delay() {
        return this.delay;
    }

    @Override
    public long timeout() {
        return this.timeout;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(toApp);
        dest.writeString(toProcess);
        dest.writeString(url);
        dest.writeInt(processorMode);
        dest.writeLong(delay);
        dest.writeLong(timeout);
    }
}

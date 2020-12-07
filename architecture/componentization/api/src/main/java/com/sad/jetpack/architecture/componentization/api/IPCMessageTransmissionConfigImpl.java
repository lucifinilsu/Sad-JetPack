package com.sad.jetpack.architecture.componentization.api;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class IPCMessageTransmissionConfigImpl implements IPCMessageTransmissionConfig, Parcelable {

    private String fromApp="";
    private String fromProcess="";
    private IPCTarget target=IPCTargetImpl.newInstance();
    private int destType=CommonConstant.REGISTER_TO_MESSENGERS_POOL;
    private IPCMessageTransmissionConfigImpl(){}
    public static IPCMessageTransmissionConfigImpl newInstance(){
        return new IPCMessageTransmissionConfigImpl();
    }
    public static IPCMessageTransmissionConfigImpl fromCurrProcess(){
        Context context=InternalContextHolder.get().getContext();
        return fromProcess(context);
    }
    public static IPCMessageTransmissionConfigImpl fromProcess(Context context){
        return newInstance().fromApp(context.getPackageName()).fromProcess(Utils.getCurrAppProccessName(context));
    }
    public IPCMessageTransmissionConfigImpl fromApp(String fromApp){
        this.fromApp=fromApp;
        return this;
    }
    public IPCMessageTransmissionConfigImpl fromProcess(String fromProcess){
        this.fromProcess=fromProcess;
        return this;
    }
    public IPCMessageTransmissionConfigImpl target(IPCTarget target){
        this.target=target;
        return this;
    }
    public IPCMessageTransmissionConfigImpl destType(int destType){
        this.destType=destType;
        return this;
    }
    protected IPCMessageTransmissionConfigImpl(Parcel in) {
        fromApp = in.readString();
        fromProcess = in.readString();
        target = in.readParcelable(IPCTarget.class.getClassLoader());
        destType=in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fromApp);
        dest.writeString(fromProcess);
        dest.writeParcelable(target, flags);
        dest.writeInt(destType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IPCMessageTransmissionConfigImpl> CREATOR = new Creator<IPCMessageTransmissionConfigImpl>() {
        @Override
        public IPCMessageTransmissionConfigImpl createFromParcel(Parcel in) {
            return new IPCMessageTransmissionConfigImpl(in);
        }

        @Override
        public IPCMessageTransmissionConfigImpl[] newArray(int size) {
            return new IPCMessageTransmissionConfigImpl[size];
        }
    };

    @Override
    public String fromApp() {
        return this.fromApp;
    }

    @Override
    public String fromProcess() {
        return this.fromProcess;
    }

    @Override
    public IPCTarget target() {
        return this.target;
    }

    @Override
    public int destType() {
        return 0;
    }
}

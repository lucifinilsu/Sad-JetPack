package com.sad.jetpack.architecture.componentization.api2;

import android.os.Parcel;
public class ResponseImpl implements IResponse, IResponse.Builder {

    private IRequest request;
    private IDataContainer dataContainer;
    private ResponseImpl(){};
    public static IResponse newInstance(){
        return new ResponseImpl();
    }
    public static IResponse.Builder newBuilder(){
        return new ResponseImpl();
    }
    protected ResponseImpl(Parcel in) {
        request = in.readParcelable(IRequest.class.getClassLoader());
        dataContainer= (IDataContainer) in.readSerializable();
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public ResponseImpl createFromParcel(Parcel in) {
            return new ResponseImpl(in);
        }

        @Override
        public ResponseImpl[] newArray(int size) {
            return new ResponseImpl[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(request, flags);
        dest.writeSerializable(dataContainer);
    }

    @Override
    public IRequest request() {
        return this.request;
    }

    @Override
    public IDataContainer dataContainer() {
        return this.dataContainer;
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
    public Builder dataContainer(IDataContainer dataContainer) {
        this.dataContainer=dataContainer;
        return this;
    }

    @Override
    public ResponseImpl build() {
        return this;
    }
}

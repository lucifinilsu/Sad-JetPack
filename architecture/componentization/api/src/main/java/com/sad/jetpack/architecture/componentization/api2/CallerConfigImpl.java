package com.sad.jetpack.architecture.componentization.api2;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

public class CallerConfigImpl implements ICallerConfig ,ICallerConfig.Builder{
    private CallerConfigImpl(){}
    private long delay=-1;
    private long timeout=-1;

    public static ICallerConfig newInstance(){
        return new CallerConfigImpl();
    }

    public static ICallerConfig.Builder newBuilder(){
        return new CallerConfigImpl();
    }

    protected CallerConfigImpl(Parcel in) {
        delay = in.readLong();
        timeout = in.readLong();
    }

    public static final Creator<CallerConfigImpl> CREATOR = new Creator<CallerConfigImpl>() {
        @Override
        public CallerConfigImpl createFromParcel(Parcel in) {
            return new CallerConfigImpl(in);
        }

        @Override
        public CallerConfigImpl[] newArray(int size) {
            return new CallerConfigImpl[size];
        }
    };

    @Override
    public long delay() {
        return this.delay;
    }

    @Override
    public long timeout() {
        return this.timeout;
    }

    @Override
    public Builder toBuilder() {
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(delay);
        dest.writeLong(timeout);
    }

    @Override
    public Builder delay(long delay) {
        this.delay=delay;
        return this;
    }

    @Override
    public Builder timeout(long timeout) {
        this.timeout=timeout;
        return this;
    }

    @Override
    public ICallerConfig build() {
        return this;
    }
}

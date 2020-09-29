package com.sad.jetpack.architecture.componentization.api.impl;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.sad.jetpack.architecture.componentization.api.DataState;
import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.security.ISecurity;
import com.sad.jetpack.security.ISecurityVerify;

import java.io.Serializable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataCarrierImpl implements IDataCarrier,IDataCarrier.Creator {

    private DataCarrierImpl(){}
    private Object data=null;
    private DataState state=DataState.UNWORKED;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    protected DataCarrierImpl(Parcel in) {
        this.data=in.readValue(getClass().getClassLoader());
        this.state= (DataState) in.readSerializable();
        this.lock=in.readParcelable(getClass().getClassLoader());
    }

    public static final Parcelable.Creator<DataCarrierImpl> CREATOR = new Parcelable.Creator<DataCarrierImpl>() {
        @Override
        public DataCarrierImpl createFromParcel(Parcel in) {
            return new DataCarrierImpl(in);
        }

        @Override
        public DataCarrierImpl[] newArray(int size) {
            return new DataCarrierImpl[size];
        }
    };

    public static IDataCarrier.Creator newInstanceCreator(){
        return new DataCarrierImpl();
    }

    @Override
    public String toString() {
        return "DataCarrierImpl{\n" +
                "data=" + data +
                ", \nstate=" + state +
                "\n}";
    }

    @Override
    public <T> T data() {
        lock.readLock().lock();
        T d= (T) data;
        lock.readLock().unlock();
        return d;
    }

    @Override
    public DataState state() {
        lock.readLock().lock();
        DataState state= this.state;
        lock.readLock().unlock();
        return state;
    }

    @Override
    public IDataCarrier.Creator creator(){
        return this;
    }

    @Override
    public IDataCarrier.Creator data(Object o) {
        lock.writeLock().lock();
        this.data=o;
        lock.writeLock().unlock();
        return this;
    }

    @Override
    public IDataCarrier.Creator state(DataState state) {
        lock.writeLock().lock();
        this.state=state;
        lock.writeLock().unlock();
        return this;
    }

    @Override
    public IDataCarrier create() {
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.data);
        dest.writeSerializable(this.state);
        dest.writeParcelable((Parcelable) this.lock,flags);
    }
}

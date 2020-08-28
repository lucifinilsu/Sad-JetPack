package com.sad.jetpack.architecture.componentization.api.impl;

import androidx.annotation.NonNull;

import com.sad.jetpack.architecture.componentization.api.DataState;
import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.security.ISecurity;
import com.sad.jetpack.security.ISecurityVerify;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataCarrierImpl implements IDataCarrier,IDataCarrier.Creator {

    private DataCarrierImpl(){}
    private Object data=null;
    private DataState state=DataState.UNWORKED;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
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
    public Creator creator(){
        return this;
    }

    @Override
    public Creator data(Object o) {
        lock.writeLock().lock();
        this.data=o;
        lock.writeLock().unlock();
        return this;
    }

    @Override
    public Creator state(DataState state) {
        lock.writeLock().lock();
        this.state=state;
        lock.writeLock().unlock();
        return this;
    }

    @Override
    public IDataCarrier create() {
        return this;
    }
}

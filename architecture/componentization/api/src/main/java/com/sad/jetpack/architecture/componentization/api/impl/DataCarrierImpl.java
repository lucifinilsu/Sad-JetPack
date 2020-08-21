package com.sad.jetpack.architecture.componentization.api.impl;

import androidx.annotation.NonNull;

import com.sad.jetpack.architecture.componentization.api.DataState;
import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.security.ISecurity;
import com.sad.jetpack.security.ISecurityVerify;

public class DataCarrierImpl implements IDataCarrier,IDataCarrier.Creator {

    private DataCarrierImpl(){}
    private Object data=null;
    private DataState state=DataState.UNWORKED;
    public static IDataCarrier.Creator newInstanceCreator(){
        return new DataCarrierImpl();
    }
    @Override
    public <T> T data() {
        return (T) data;
    }

    @Override
    public DataState state() {
        return this.state;
    }

    @Override
    public Creator creator(){
        return this;
    }

    @Override
    public Creator data(Object o) {
        this.data=o;
        return this;
    }

    @Override
    public Creator state(DataState state) {
        this.state=state;
        return this;
    }

    @Override
    public IDataCarrier create() {
        return this;
    }
}

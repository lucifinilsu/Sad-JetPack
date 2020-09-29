package com.sad.jetpack.architecture.componentization.api.internal;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.sad.jetpack.architecture.componentization.api.CommonConstant;
import com.sad.jetpack.architecture.componentization.api.DataState;
import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.architecture.componentization.api.IPerformer;
import com.sad.jetpack.architecture.componentization.api.IProceedListener;
import com.sad.jetpack.architecture.componentization.api.ILocalProcessor;
import com.sad.jetpack.architecture.componentization.api.IRemoteProcessor;
import com.sad.jetpack.architecture.componentization.api.impl.DataCarrierImpl;
import com.sad.jetpack.architecture.componentization.api.remote.IPCTransmissionImpl;
import com.sad.jetpack.architecture.componentization.api.remote.IPCWorkBenchImpl;

import static com.sad.jetpack.architecture.componentization.api.remote.IPCTransmission.CREATE_REMOTE_IPC_CHAT;

public class InternalRemoteProcessor implements IRemoteProcessor<InternalRemoteProcessor>,IPerformer{

    private int proceedMode = ILocalProcessor.PROCEED_MODE_SEQUENCE;
    private long timeout=-1;
    private IProceedListener proceedListener;
    private String appPkg="";
    private String processName="";
    private String orgUrl="";
    private Context context;


    public InternalRemoteProcessor(Context context,String appPkg, String processName, String orgUrl) {
        this.appPkg = appPkg;
        this.processName = processName;
        this.orgUrl=orgUrl;
        this.context=context;
    }

    @Override
    public InternalRemoteProcessor proceedMode(int processMode) {
        this.proceedMode=processMode;
        return this;
    }


    @Override
    public InternalRemoteProcessor timeout(long timeout) {
        this.timeout=timeout;
        return this;
    }

    @Override
    public @NonNull IPerformer submit() {
        return this;
    }

    @Override
    public InternalRemoteProcessor listener(IProceedListener callerListener) {
        this.proceedListener =callerListener;
        return this;
    }

    @Override
    public void start(@NonNull IDataCarrier data, boolean restart, long delay) {
        try {
            if (data==null) {
                data = DataCarrierImpl.newInstanceCreator().state(DataState.UNWORKED).create();
            }
            if (proceedListener!=null){
                data=proceedListener.onInput(data);
            }
            Bundle bundle=data.data();
            if (bundle==null){
                bundle=new Bundle();
            }
            bundle.putLong(CommonConstant.REMOTE_BUNDLE_PARAMETERS_TIMEOUT,this.timeout);
            bundle.putString(CommonConstant.REMOTE_BUNDLE_PARAMETERS_ORGURL,this.orgUrl);
            bundle.putInt(CommonConstant.REMOTE_BUNDLE_PARAMETERS_PROCEED_MODE,this.proceedMode);
            bundle.putBoolean(CommonConstant.REMOTE_BUNDLE_PARAMETERS_PROCEED_FROM_INTERCEPTED,!restart);
            IPCWorkBenchImpl.sendMessage(context,CREATE_REMOTE_IPC_CHAT,this.appPkg,this.processName,bundle,this.proceedListener,this);
        }catch (Exception e){
            e.printStackTrace();
            if (proceedListener !=null){
                proceedListener.onExceptionInPerformer(e);
            }
        }
    }
}

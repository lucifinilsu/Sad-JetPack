package com.sad.jetpack.demo.c;

import android.util.Log;

import com.sad.jetpack.architecture.componentization.annotation.ExposedService;
import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IPCMessenger;
import com.sad.jetpack.architecture.componentization.api.IPCSession;
import com.sad.jetpack.architecture.componentization.api.impl.DataCarrierImpl;

@ExposedService(url = "test://group/c/s/4")
public class C_S_4 implements IExposedService {
    @Override
    public <T> T action(IPCMessenger messenger) {
        Log.e("sad-jetpack","------------->并行同步任务4");
        IDataCarrier dataCarrier=messenger.extraMessage();
        if (dataCarrier==null){
            dataCarrier= DataCarrierImpl.newInstanceCreator().data("c_s_4").create();
        }
        dataCarrier.creator().data("c_s_4").create();
        messenger.reply(dataCarrier, new IPCSession() {
            @Override
            public boolean componentChat(IDataCarrier o, IPCMessenger messenger) {
                return false;
            }
        });
        return null;
    }

    @Override
    public int priority() {
        return 96;
    }
}

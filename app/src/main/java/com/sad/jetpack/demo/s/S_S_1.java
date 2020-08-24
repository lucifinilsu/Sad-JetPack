package com.sad.jetpack.demo.s;

import android.util.Log;

import com.sad.jetpack.architecture.componentization.annotation.ExposedService;
import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IPCMessenger;
import com.sad.jetpack.architecture.componentization.api.IPCSession;
import com.sad.jetpack.architecture.componentization.api.impl.DataCarrierImpl;

@ExposedService(url = "test://group/s/s/1")
public class S_S_1 implements IExposedService {
    @Override
    public <T> T action(IPCMessenger messenger) {
        Log.e("sad-jetpack","------------->串行同步任务1");
        IDataCarrier dataCarrier=messenger.extraMessage();
        if (dataCarrier==null){
            dataCarrier= DataCarrierImpl.newInstanceCreator().data("s_s_1").create();
        }
        dataCarrier.creator().data("s_s_1").create();
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
        return 99;
    }
}

package com.sad.jetpack.demo.s;

import android.util.Log;

import com.sad.core.async.ISADTaskProccessListener;
import com.sad.core.async.SADTaskRunnable;
import com.sad.core.async.SADTaskSchedulerClient;
import com.sad.jetpack.architecture.componentization.annotation.ExposedService;
import com.sad.jetpack.architecture.componentization.api.IDataCarrier;
import com.sad.jetpack.architecture.componentization.api.IExposedService;
import com.sad.jetpack.architecture.componentization.api.IPCMessenger;
import com.sad.jetpack.architecture.componentization.api.IPCSession;
import com.sad.jetpack.architecture.componentization.api.impl.DataCarrierImpl;

@ExposedService(url = "test://group/s/a/2")
public class S_A_2 implements IExposedService {
    @Override
    public <T> T action(IPCMessenger messenger) {
        Log.e("sad-jetpack","------------->串行异步任务2");
        SADTaskSchedulerClient.newInstance()
                .execute(new SADTaskRunnable<IDataCarrier>("sa2", new ISADTaskProccessListener<IDataCarrier>() {
                    @Override
                    public void onSuccess(IDataCarrier result) {
                        messenger.reply(result, new IPCSession() {
                            @Override
                            public boolean componentChat(IDataCarrier o, IPCMessenger messenger) {
                                return false;
                            }
                        });
                    }

                    @Override
                    public void onFail(Throwable throwable) {

                    }

                    @Override
                    public void onCancel() {

                    }
                }) {
                    @Override
                    public IDataCarrier doInBackground() throws Exception {
                        IDataCarrier dataCarrier=messenger.extraMessage();
                        if (dataCarrier==null){
                            dataCarrier= DataCarrierImpl.newInstanceCreator().data("s_a_2").create();
                        }
                        dataCarrier.creator().data("s_a_2").create();
                        Thread.sleep(5000);
                        return dataCarrier;
                    }
                });

        return null;
    }

    @Override
    public int priority() {
        return 98;
    }
}

package com.sad.jetpack.demo;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sad.jetpack.architecture.appgo.api.AppGo;
import com.sad.jetpack.architecture.componentization.annotation.ActivityRouter;
import com.sad.jetpack.architecture.componentization.api.BodyImpl;
import com.sad.jetpack.architecture.componentization.api.IPCRemoteCallListener;
import com.sad.jetpack.architecture.componentization.api.IRequest;
import com.sad.jetpack.architecture.componentization.api.IRequestSession;
import com.sad.jetpack.architecture.componentization.api.IResponse;
import com.sad.jetpack.architecture.componentization.api.ITarget;
import com.sad.jetpack.architecture.componentization.api.LogcatUtils;
import com.sad.jetpack.architecture.componentization.api.ParasiticComponentRepositoryFactory;
import com.sad.jetpack.architecture.componentization.api.ProcessorMode;
import com.sad.jetpack.architecture.componentization.api.RemoteAction;
import com.sad.jetpack.architecture.componentization.api.RequestImpl;
import com.sad.jetpack.architecture.componentization.api.SCore;
import com.sad.jetpack.architecture.componentization.api.StaticComponentRepositoryFactory;
import com.sad.jetpack.architecture.componentization.api.TargetImpl;
import com.sad.jetpack.architecture.componentization.api.Utils;
@ActivityRouter(url = "activity://demo/remote/1")
public class RemoteActivity extends AppCompatActivity {
    TextView tv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=findViewById(R.id.myProcess);
        tv.setText("当前App:"+getApplicationContext().getPackageName()+"\n当前进程:"+ Utils.getCurrAppProccessName(getApplicationContext()));
        findViewById(R.id.shap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testPostRemoteMsg();
                /*Context context=textInitContext();
                tv.setText("当前App:"+context.getPackageName()+"\n当前进程:"+ Utils.getCurrAppProccessName(context));*/
            }
        });
    }

    private Context textInitContext(){
        return /*InternalContextHolder.get().getContext()*/null;
    }

    public void testPostRemoteMsg(){
        try {
            SCore.ipc(getApplicationContext())
                    .action(RemoteAction.REMOTE_ACTION_CREATE_REMOTE_IPC_CHAT)
                    .request(RequestImpl
                            .newBuilder("_remote_")
                            .fromProcess(Utils.getCurrAppProccessName(getApplicationContext()))
                            .fromApp(getApplicationContext().getPackageName())
                            .body(BodyImpl.newBuilder().addData("xxx","来自远方的朋友").build())
                            .build()
                    )
                    .instancesFactory(StaticComponentRepositoryFactory.newInstance())
                    .target(TargetImpl.newBuilder()
                            .toProcess(getApplicationContext().getPackageName())
                            .toApp(getApplicationContext().getPackageName())
                            .id("xxx://ssss.php.cn/java/base7/index?dww=cs")
                            .processorMode(ProcessorMode.PROCESSOR_MODE_SINGLE)
                            .build()
                    )
                    .listener(new IPCRemoteCallListener() {
                        @Override
                        public boolean onRemoteCallReceivedResponse(IResponse response, IRequestSession session, ITarget target) {
                            LogcatUtils.e("收到了来自主进程的回应："+response.body().dataContainer().getMap());
                            //session.replyRequestData(response.dataContainer().put("remote","ooooooooooo"));
                            return false;
                        }

                        @Override
                        public void onRemoteCallException(IRequest request, Throwable throwable, ITarget target) {
                            LogcatUtils.e("收到了来自主进程的回应，发生错误："+throwable.getMessage());
                        }
                    })
                    .build()
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

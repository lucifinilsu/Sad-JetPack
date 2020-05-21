package com.sad.jetpack.demo;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.sad.jetpack.architecture.appgo.api.AppGo;
import com.sad.jetpack.architecture.componentization.api.ExposdServiceRelationMappingEntity;
import com.sad.jetpack.architecture.componentization.api.ESRManager;
import com.sad.jetpack.architecture.componentization.api.IExposedActionNotifier;
import com.sad.jetpack.architecture.componentization.api.IExposedService;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String s="";
        AppGo.get().getApplication();

        try {
            Map<String, ExposdServiceRelationMappingEntity> map= ESRManager
                    .get()
                    .handle("xxx://ssss.php.cn/java/")
                    .create()
                    .entityGroup()
                    ;
            Log.e("sad-jetpack","entity列表:"+map);

            IExposedService exposedService=ESRManager.exposedServiceFirst("xxx://ssss.php.cn/java/base7/index?dww=cs").instance();
            exposedService.action(new IExposedActionNotifier() {
                @Override
                public boolean notifyBy(Object... o) {
                    Log.e("sad-jetpack","接收事件模块回调");
                    return false;
                }
            });

            Map<String,Class> m=ESRManager.get()
                    .handle("xxx://ssss.php.cn/java/")
                    .create()
                    .repository()
                    .serviceClassList();
            Log.e("sad-jetpack","Class列表："+m);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

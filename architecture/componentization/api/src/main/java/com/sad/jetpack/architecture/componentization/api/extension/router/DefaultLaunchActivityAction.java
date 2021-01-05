package com.sad.jetpack.architecture.componentization.api.extension.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DefaultLaunchActivityAction implements ILaunchActivityAction{

    @Override
    public boolean doStartActivity(Context context, Intent intent, IActivityRouterParameters params) throws Exception{
        try{
            if(context!=null) {
                //intent.setClass(context, targetActivityClass());
                boolean isForResult=params!=null && params.resultLauncher()!=null;
                int requestCode=params!=null?params.requestCode():-99999;
                Bundle options=params!=null?params.options():null;
                ActivityResultLauncher activityResultLauncher=params!=null?params.resultLauncher():null;
                if (!(context instanceof Activity)){
                    if (isForResult){
                        throw new Exception("you should use activity instead of context when you want to startForResult !!!!");
                    }
                    ContextCompat.startActivity(context,intent,options);
                    return true;

                }
                else{
                    if (isForResult){
                        activityResultLauncher.launch(intent);
                    }
                    else{
                        ActivityCompat.startActivity(context,intent,options);
                    }
                    return true;
                }
            }
            else{
                throw new Exception("context == null !!!");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;

        }

    }
    @Override
    public boolean doStartActivities(Context context, Intent[] intent,IActivityRouterParameters params) throws Exception{
        try{
            if(context!=null) {
                boolean isForResult=params!=null && params.resultLauncher()!=null;
                int requestCode=params!=null?params.requestCode():-99999;
                Bundle options=params!=null?params.options():null;
                ActivityResultLauncher activityResultLauncher=params!=null?params.resultLauncher():null;

                if (!(context instanceof Activity)){
                    if (isForResult){
                        throw new Exception("you should use activity instead of context when you want to startForResult !!!!");
                    }
                    ContextCompat.startActivities(context,intent,options);
                    return true;

                }
                else{
                    if (isForResult){
                        activityResultLauncher.launch(intent);
                    }
                    else{
                        ActivityCompat.startActivities(context,intent,options);
                    }
                    return true;
                }
            }
            else{
                throw new Exception("context == null !!!");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;

        }

    }
}

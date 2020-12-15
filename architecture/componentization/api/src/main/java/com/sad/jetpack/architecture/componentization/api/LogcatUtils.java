package com.sad.jetpack.architecture.componentization.api;


import android.util.Log;

public class LogcatUtils {
    public static void e(String tag,String log){
        if (CommonConstant.enableLog){
            Log.e(tag,log);
        }
    }
    public static void e(String log){
        e("SAD-JETPACK",log);
    }
}

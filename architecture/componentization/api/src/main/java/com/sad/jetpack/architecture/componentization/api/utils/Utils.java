package com.sad.jetpack.architecture.componentization.api.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.sad.jetpack.architecture.componentization.annotation.EncryptUtil;

import java.util.List;

public class Utils {
    private final static String SP="122savba1565_cer";
    private final static String DEFAULT_KEY="34324ffqfaw";
    public static String encodeMessengerId(String orgUrl,String mark){
        String o=orgUrl+SP+mark;
        String e= EncryptUtil.getInstance().XORencode(o,DEFAULT_KEY);
        return e;
    }
    public static String decodeMessengerId(String messengerId){
        String d= EncryptUtil.getInstance().XORdecode(messengerId,DEFAULT_KEY);
        return d;
    }
    public static String getOrgUrlFromMessengerId(String messengerId ){
        String d=decodeMessengerId(messengerId);
        String[] os=d.split(SP);
        if (os!=null && os.length>0){
            return os[0];
        }
        return "";
    }
    public static String getMarkFromMessengerId(String messengerId ){
        String d=decodeMessengerId(messengerId);
        String[] os=d.split(SP);
        if (os!=null && os.length>1){
            return os[1];
        }
        return "";
    }

    /**
     * 获取当前运行的进程名
     * @param context
     * @return
     */
    public static String getCurrAppProccessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {

                return appProcess.processName;
            }
        }
        return "";
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}

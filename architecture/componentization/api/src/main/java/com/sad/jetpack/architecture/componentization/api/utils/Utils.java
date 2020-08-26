package com.sad.jetpack.architecture.componentization.api.utils;

import com.sad.jetpack.architecture.componentization.annotation.EncryptUtil;

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
}

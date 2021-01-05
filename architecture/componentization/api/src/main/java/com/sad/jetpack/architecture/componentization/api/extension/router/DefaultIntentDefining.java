package com.sad.jetpack.architecture.componentization.api.extension.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.List;

public class DefaultIntentDefining implements IntentDefining{

    public Intent intentFromParameters(Context context, Intent intent, IActivityRouterParameters parameters, ClassLoader loader){
        if (parameters==null){
            return intent;
        }
        //action
        String action=parameters.action();
        if (!TextUtils.isEmpty(action)){
            intent.setAction(action);
        }
        //flags
        List<Integer> flags=parameters.flags();
        if (flags!=null){
            for (Integer flag:flags
            ) {
                intent.addFlags(flag);
            }

        }
        if (!(context instanceof Activity)){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        //targetPackage
        String targetPackage=parameters.targetPackage();
        if (!TextUtils.isEmpty(targetPackage)){
            intent.setPackage(targetPackage);
        }
        //bundle
        Bundle bundle = parameters.bundle();
        intent.setExtrasClassLoader(loader);
        if (bundle!=null){
            intent.putExtras(bundle);
        }
        //uri
        Uri uri=parameters.uri();
        if (uri!=null){
            intent.setData(uri);
        }
        return intent;
    }

}

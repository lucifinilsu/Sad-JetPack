package com.sad.jetpack.architecture.componentization.api.extension.router;

import android.content.Intent;
import android.net.Uri;

public interface IActivityLauncherMaster {

    IActivityLauncherMaster parameters(IActivityRouterParameters parameters);

    IActivityLauncherMaster useSystemStackPerforming(boolean u);

    IActivityLauncherMaster preIntent(Intent intent);

    IActivityLauncherMaster launchActivityAction(ILaunchActivityAction launchActivityAction);

    IActivityLauncherMaster intentDefining(IntentDefining intentDefining);

    default void start(Uri uri){
        if (uri!=null){
            start(uri.toString());
        }
    }

    void start(String url);

}

package com.sad.jetpack.architecture.componentization.api.extension.router;

import android.content.Context;
import android.content.Intent;

public interface ILaunchActivityAction {

    boolean doStartActivities(Context context, Intent[] intent, IActivityRouterParameters params) throws Exception;

    boolean doStartActivity(Context context, Intent intent, IActivityRouterParameters params) throws Exception;

}

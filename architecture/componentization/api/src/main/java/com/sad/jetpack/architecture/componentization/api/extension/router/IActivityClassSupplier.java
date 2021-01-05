package com.sad.jetpack.architecture.componentization.api.extension.router;

import android.app.Activity;

public interface IActivityClassSupplier {

    String INTENT_PRIORITY="INTENT_PRIORITY";

    Class<? extends Activity> activityClass();

    int intentPriority();

}

package com.sad.jetpack.architecture.componentization.api2;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import static com.sad.jetpack.architecture.componentization.api2.RemoteActionResultState.*;


@IntDef({RemoteActionResultState.REMOTE_ACTION_RESULT_STATE_SUCCESS, REMOTE_ACTION_RESULT_STATE_FAILURE})
@Retention(RetentionPolicy.SOURCE)
public @interface RemoteActionResultState {

    int REMOTE_ACTION_RESULT_STATE_SUCCESS=200;

    int REMOTE_ACTION_RESULT_STATE_FAILURE=500;

}

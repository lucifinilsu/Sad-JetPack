package com.sad.jetpack.architecture.componentization.api2;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.sad.jetpack.architecture.componentization.api2.RemoteAction.*;


@IntDef({RemoteAction.REMOTE_ACTION_REGISTER_TO_MESSENGERS_POOL, REMOTE_ACTION_UNREGISTER_FROM_MESSENGERS_POOL,REMOTE_ACTION_CREATE_REMOTE_IPC_CHAT})
@Retention(RetentionPolicy.SOURCE)
public @interface RemoteAction {

    int REMOTE_ACTION_REGISTER_TO_MESSENGERS_POOL=1;

    int REMOTE_ACTION_UNREGISTER_FROM_MESSENGERS_POOL=2;

    int REMOTE_ACTION_CREATE_REMOTE_IPC_CHAT =4;
}

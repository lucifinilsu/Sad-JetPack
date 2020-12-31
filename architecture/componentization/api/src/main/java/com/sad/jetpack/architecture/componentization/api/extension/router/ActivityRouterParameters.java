package com.sad.jetpack.architecture.componentization.api.extension.router;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;

import androidx.activity.result.ActivityResultLauncher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityRouterParameters implements IActivityRouterParameters<ActivityRouterParameters,ActivityRouterParameters>, IActivityRouterParameters.Builder<ActivityRouterParameters,ActivityRouterParameters> {

    private Bundle transitionBundle;
    private int requestCode;
    private ActivityResultLauncher resultLauncher;
    private String action="";
    private List<Integer> flags=new ArrayList<>();
    private Bundle bundle;
    private String targetPackage="";
    private Uri uri;
    private ActivityRouterParameters(){}

    public static IActivityRouterParameters<? extends IActivityRouterParameters,? extends IActivityRouterParameters.Builder> newInstance(){
        return new ActivityRouterParameters();
    }
    public static final Creator<ActivityRouterParameters> CREATOR = new Creator<ActivityRouterParameters>() {
        @Override
        public ActivityRouterParameters createFromParcel(Parcel in) {
            ActivityRouterParameters activityRouterParameters = new ActivityRouterParameters();
            activityRouterParameters.readFromParcel(in);
            return activityRouterParameters;
        }

        @Override
        public ActivityRouterParameters[] newArray(int size) {
            return new ActivityRouterParameters[size];
        }
    };

    @Override
    public Bundle transitionBundle() {
        return this.transitionBundle;
    }

    @Override
    public int requestCode() {
        return this.requestCode;
    }

    @Override
    public ActivityResultLauncher resultLauncher() {
        return this.resultLauncher;
    }

    @Override
    public String action() {
        return this.action;
    }

    @Override
    public List<Integer> flags() {
        return this.flags;
    }

    @Override
    public Bundle bundle() {
        return this.bundle;
    }

    @Override
    public String targetPackage() {
        return this.targetPackage;
    }

    @Override
    public Uri uri() {
        return this.uri;
    }

    @Override
    public ActivityRouterParameters toBuilder() {
        return this;
    }

    @Override
    public ActivityRouterParameters transitionBundle(Bundle transitionBundle) {
        this.transitionBundle=transitionBundle;
        return this;
    }

    @Override
    public ActivityRouterParameters requestCode(int code) {
        this.requestCode=code;
        return this;
    }

    @Override
    public ActivityRouterParameters resultLauncher(ActivityResultLauncher resultLauncher) {
        this.resultLauncher=resultLauncher;
        return this;
    }

    @Override
    public ActivityRouterParameters action(String action) {
        this.action=action;
        return this;
    }

    @Override
    public ActivityRouterParameters addFlags(Integer... flags) {
        if (flags!=null && flags.length>0){
            this.flags.addAll(Arrays.asList(flags));
        }
        return this;
    }

    @Override
    public ActivityRouterParameters addFlags(ArrayList<Integer> flags) {
        if (flags!=null && flags.size()>0){
            this.flags.addAll(flags);
        }
        return this;
    }

    @Override
    public ActivityRouterParameters bundle(Bundle bundle) {
        this.bundle=bundle;
        return this;
    }

    @Override
    public ActivityRouterParameters targetPackage(String pkg) {
        this.targetPackage=pkg;
        return this;
    }

    @Override
    public ActivityRouterParameters uri(Uri uri) {
        this.uri=uri;
        return this;
    }

    @Override
    public ActivityRouterParameters build() {
        return this;
    }
}

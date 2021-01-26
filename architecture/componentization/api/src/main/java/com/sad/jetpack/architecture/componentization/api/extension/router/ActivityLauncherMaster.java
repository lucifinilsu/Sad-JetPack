package com.sad.jetpack.architecture.componentization.api.extension.router;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.sad.jetpack.architecture.componentization.api.CommonConstant;
import com.sad.jetpack.architecture.componentization.api.IComponentsCluster;
import com.sad.jetpack.architecture.componentization.api.LogcatUtils;
import com.sad.jetpack.architecture.componentization.api.MapTraverseUtils;
import com.sad.jetpack.architecture.componentization.api.SCore;
import com.sad.jetpack.architecture.componentization.api.StaticComponentRepositoryFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class ActivityLauncherMaster implements IActivityLauncherMaster{

    private Context context;
    private Fragment fragment;
    private IActivityRouterParameters parameters;
    private boolean useSystemStackPerforming=true;
    private Intent preIntent;
    private ILaunchActivityAction launchActivityAction=new DefaultLaunchActivityAction();
    private IntentDefining intentDefining=new DefaultIntentDefining();

    private ActivityLauncherMaster(){}
    private ActivityLauncherMaster(Context context){
        this.context=context;
    }
    private ActivityLauncherMaster(Fragment fragment){
        this.fragment=fragment;
    }

    public static IActivityLauncherMaster newInstance(Context context){
        return new ActivityLauncherMaster(context);
    }

    public static IActivityLauncherMaster newInstance(Fragment fragment){
        return new ActivityLauncherMaster(fragment);
    }

    @Override
    public IActivityLauncherMaster parameters(IActivityRouterParameters parameters) {
        this.parameters=parameters;
        return this;
    }

    @Override
    public IActivityLauncherMaster useSystemStackPerforming(boolean u) {
        this.useSystemStackPerforming=u;
        return this;
    }

    @Override
    public IActivityLauncherMaster preIntent(Intent intent) {
        this.preIntent=intent;
        return this;
    }

    @Override
    public IActivityLauncherMaster launchActivityAction(ILaunchActivityAction launchActivityAction) {
        this.launchActivityAction=launchActivityAction;
        return this;
    }

    @Override
    public IActivityLauncherMaster intentDefining(IntentDefining intentDefining) {
        this.intentDefining=intentDefining;
        return this;
    }

    @Override
    public void start(String url) {
        if (TextUtils.isEmpty(url)){
            return;
        }
        Context context=this.context!=null?this.context:(this.fragment!=null?fragment.requireActivity():null);
        if (context==null){
            return;
        }
        IComponentsCluster cluster= SCore.getCluster(context);
        LinkedHashMap<Object,String> os=cluster.instancesRepositoryFactory(StaticComponentRepositoryFactory.newInstance())
        .repository(url)
        .objectInstances()
        ;
        if (os.isEmpty()){
            return;
        }
        if (preIntent==null){
            preIntent=new Intent();
        }
        Intent intent=preIntent;
        if (os.size()==1){
            Object o= new ArrayList<Object>(os.keySet()).get(0);
            if (o instanceof IActivityClassSupplier){
                IActivityClassSupplier classSupplier= (IActivityClassSupplier)o;
                intent.setClass(context,classSupplier.activityClass());
                if (intentDefining!=null){
                    intent=intentDefining.intentFromParameters(context,intent,parameters,classSupplier.activityClass().getClassLoader());
                }
                try {
                    if (launchActivityAction!=null){
                        launchActivityAction.doStartActivity(context,intent,parameters);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        else {
            List<Intent> intents=new ArrayList<>();
            Intent finalIntent = new Intent(intent);
            MapTraverseUtils.traverseGroup(os, new MapTraverseUtils.ITraverseAction<Object, String>() {

                @Override
                public void onTraversed(Object o, String s) {
                    Intent i= new Intent(finalIntent);
                    if (o instanceof IActivityClassSupplier){
                        IActivityClassSupplier classSupplier= (IActivityClassSupplier)o;
                        i.setClass(context,classSupplier.activityClass());
                        i.putExtra(IActivityClassSupplier.INTENT_PRIORITY,classSupplier.intentPriority());
                        if (intentDefining!=null){
                            i=intentDefining.intentFromParameters(context,i,parameters,classSupplier.activityClass().getClassLoader());
                        }
                        intents.add(i);
                    }
                }
            });
            try {
                if (!intents.isEmpty()){
                    Collections.sort(intents, new Comparator<Intent>() {
                        @Override
                        public int compare(Intent o1, Intent o2) {
                            return
                                    useSystemStackPerforming?
                                            o1.getIntExtra(IActivityClassSupplier.INTENT_PRIORITY,0)-o2.getIntExtra(IActivityClassSupplier.INTENT_PRIORITY,0)
                                            :
                                            o2.getIntExtra(IActivityClassSupplier.INTENT_PRIORITY,0)-o1.getIntExtra(IActivityClassSupplier.INTENT_PRIORITY,0)
                                    ;
                        }
                    });
                    /*for (Intent ii:intents
                         ) {

                        LogcatUtils.e(">>>意图队列成员_"+intents.indexOf(ii)+":"+intent);
                    }*/
                    if (useSystemStackPerforming){
                        if (launchActivityAction!=null){
                            launchActivityAction.doStartActivities(context,intents.toArray(new Intent[intents.size()]),parameters);
                        }

                    }
                    else {
                        for (Intent in :intents) {
                            if (launchActivityAction!=null){
                                launchActivityAction.doStartActivity(context,in,parameters);
                            }

                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


}

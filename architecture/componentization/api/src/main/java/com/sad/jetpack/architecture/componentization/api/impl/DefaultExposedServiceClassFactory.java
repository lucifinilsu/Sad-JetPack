package com.sad.jetpack.architecture.componentization.api.impl;

import android.text.TextUtils;

import com.sad.jetpack.architecture.componentization.api.ExposedServiceRelationMappingElement;
import com.sad.jetpack.architecture.componentization.api.IExposedServiceClassFactory;

public class DefaultExposedServiceClassFactory implements IExposedServiceClassFactory {
    @Override
    public Class getServiceClass(ExposedServiceRelationMappingElement element) {
        if (element!=null){
            String cn=element.getClassName();
            if (!TextUtils.isEmpty(cn)){
                try {
                    Thread.currentThread().setContextClassLoader(element.getClass().getClassLoader());
                    Class cls=Class.forName(cn,true,element.getClass().getClassLoader());
                    return cls;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}

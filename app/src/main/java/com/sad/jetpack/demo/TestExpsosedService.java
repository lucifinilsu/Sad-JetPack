package com.sad.jetpack.demo;

import android.util.Log;

import com.sad.jetpack.architecture.componentization.annotation.ExposedService;

@ExposedService(url = "cs://scs.cc/sca/232/53")
public class TestExpsosedService {
    public void doSth(){
        Log.e("sad-jetpack",">>>>hellO");
    }
}

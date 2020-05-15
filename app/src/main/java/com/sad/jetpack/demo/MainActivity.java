package com.sad.jetpack.demo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sad.jetpack.architecture.appgo.api.AppGo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String s="";
        AppGo.get().getApplication();

    }
}

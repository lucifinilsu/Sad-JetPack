package com.sad.jetpack.test.module1;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sad.jetpack.architecture.componentization.annotation.ActivityRouter;

@ActivityRouter(url = "activity://demo/local/2",priority = 999)
public class Activity_2 extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
    }
}

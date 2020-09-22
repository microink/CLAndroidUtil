package com.microink.clandroidutil;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.microink.clandroid.android.executor.AppExecutors;
import com.microink.clandroid.android.screen.ScreenUtil;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int activityHeight = ScreenUtil.getActivityHeight(this);
        int screenHeight = ScreenUtil.getScreenHeight(this);
        //Log.i("Cass", "");
    }
}
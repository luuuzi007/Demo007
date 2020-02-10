package com.example.demo007;

import android.app.Application;

import com.example.arouter.Arouter;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Arouter.newInstance().init(this);
    }
}

package com.example.aidl;

import android.app.Application;
import android.util.Log;

/**
 * Created by xuqianqian on 2018/1/5.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("xx","myApplicationo is invoke");
    }
}

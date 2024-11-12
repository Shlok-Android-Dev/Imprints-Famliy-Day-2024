package com.runner.printdemo;

import android.app.Application;
import android.content.Context;

public final class PrintDemo extends Application {

    private static Context appContext;

    public static Context getAppContext() {

        return PrintDemo.appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PrintDemo.appContext = getApplicationContext();
    }
}

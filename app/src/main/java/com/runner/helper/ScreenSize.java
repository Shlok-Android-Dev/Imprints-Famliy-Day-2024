package com.runner.helper;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class ScreenSize {
    DisplayMetrics dm = new DisplayMetrics();
    private Activity activity;
    private Context context;

    public ScreenSize(Activity activity) {
        this.activity = activity;
        this.activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    }



    public int getScreenWidth(){
        return dm.widthPixels;
    }

    public int getScreenHeight(){
        return dm.heightPixels;
    }

    public double getScreenInc(){
        int dens = dm.densityDpi;

        double w = (double)getScreenWidth()/(double)dens;
        double h = (double)getScreenHeight()/(double)dens;

        double x = Math.pow(w, 2);
        double y = Math.pow(h, 2);

        return Math.sqrt(x + y);
    }
}

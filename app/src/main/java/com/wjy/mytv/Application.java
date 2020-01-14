package com.wjy.mytv;

import android.util.DisplayMetrics;

/**
 * Created by fhvgfdd on 2019/1/10.
 */

public class Application extends android.app.Application {

    private static Application instance;
    public static DisplayMetrics metrics;
    public static int screenWidth;//屏幕宽
    public static int screenHeigh;//屏幕高

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        metrics = getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;//屏幕宽
        screenHeigh = metrics.heightPixels;//屏幕高
    }

    public static Application getInstance() {
        return instance;
    }
}

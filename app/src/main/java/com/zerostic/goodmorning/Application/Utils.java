package com.zerostic.goodmorning.Application;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

/**
 Coded by iamjayantjha
 **/

public class Utils {
    public static void blackIconStatusBar(Activity activity, int colour){
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity,colour));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void navigationBar(Activity activity, int colour){
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity,colour));
    }
    public static void weatherActivityStatusBar(Activity activity, int colour){
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity,colour));
    }
}

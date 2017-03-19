package me.liuyun.bjutlgn.util;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.StyleRes;

import java.util.ArrayList;

import java8.util.stream.StreamSupport;
import me.liuyun.bjutlgn.R;
import me.liuyun.bjutlgn.ui.EasterEggActivity;
import me.liuyun.bjutlgn.ui.StatusLockedActivity;

public class ThemeHelper implements Application.ActivityLifecycleCallbacks {
    private ArrayList<Activity> activityList = new ArrayList<>();
    @StyleRes private int currentStyle = 0;
    private static ThemeHelper instance = null;

    private ThemeHelper() {
    }

    public static ThemeHelper getInstance() {
        if (instance == null) {
            instance = new ThemeHelper();
        }
        return instance;
    }

    public int getCurrentStyle() {
        return currentStyle;
    }

    public void init(Application application, @StyleRes int styleRes) {
        application.registerActivityLifecycleCallbacks(this);
        this.currentStyle = styleRes;
    }

    public void setTheme(Activity activity, @StyleRes int styleRes) {
        currentStyle = styleRes;
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putInt("theme", currentStyle).apply();
        StreamSupport.stream(activityList).filter(a -> a != activity).forEach(Activity::recreate);
        Intent intent = new Intent(activity, activity.getClass());
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        activity.finish();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        activityList.add(activity);
        if (activity.getClass().getSimpleName().equals(StatusLockedActivity.class.getSimpleName()))
            activity.setTheme(R.style.AppTheme_Dialog);
        else if(activity.getClass().getSimpleName().equals(EasterEggActivity.class.getSimpleName()))
            activity.setTheme(android.R.style.Theme_Wallpaper_NoTitleBar);
        else
            activity.setTheme(currentStyle);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activityList.remove(activity);
    }
}

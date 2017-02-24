package me.liuyun.bjutlgn.util;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import java8.util.stream.StreamSupport;
import me.liuyun.bjutlgn.R;

public class ThemeHelper implements Application.ActivityLifecycleCallbacks {
    private ArrayList<Activity> activityList = new ArrayList<>();
    private int currentTheme = 0;
    private static ThemeHelper instance = null;

    private ThemeHelper() {
    }

    public static ThemeHelper getInstance() {
        if (instance == null) {
            instance = new ThemeHelper();
        }
        return instance;
    }

    public int getCurrentTheme() {
        return currentTheme;
    }

    public void init(Application application, int theme) {
        application.registerActivityLifecycleCallbacks(this);
        this.currentTheme = theme;
    }

    public void setTheme(Activity activity, int styleRes) {
        currentTheme = styleRes;
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putInt("theme", currentTheme).apply();
        StreamSupport.stream(activityList).filter(a -> a != activity).forEach(Activity::recreate);
        Intent intent = new Intent(activity, activity.getClass());
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        activity.finish();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        activityList.add(activity);
        activity.setTheme(currentTheme);
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

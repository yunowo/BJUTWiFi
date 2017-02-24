package me.liuyun.bjutlgn;

import android.app.Application;
import android.preference.PreferenceManager;

import me.liuyun.bjutlgn.util.ThemeHelper;

public class WiFiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ThemeHelper.getInstance().init(this, PreferenceManager.getDefaultSharedPreferences(this).getInt("theme", 0));
    }

}

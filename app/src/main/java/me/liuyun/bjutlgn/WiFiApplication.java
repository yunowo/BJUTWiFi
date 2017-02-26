package me.liuyun.bjutlgn;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import me.liuyun.bjutlgn.db.FlowManager;
import me.liuyun.bjutlgn.util.ThemeHelper;

public class WiFiApplication extends Application {
    private FlowManager flowManager;
    private Resources res;
    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        res = getResources();
        flowManager = new FlowManager(this);
        ThemeHelper.getInstance().init(this, prefs.getInt("theme", R.style.ThemeBlue));
    }

    public FlowManager getFlowManager() {
        return flowManager;
    }

    public Resources getRes() {
        return res;
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }
}

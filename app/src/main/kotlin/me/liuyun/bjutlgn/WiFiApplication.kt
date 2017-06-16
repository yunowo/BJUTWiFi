package me.liuyun.bjutlgn

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import me.liuyun.bjutlgn.db.FlowManager
import me.liuyun.bjutlgn.util.ThemeHelper

class WiFiApplication : Application() {
    lateinit var flowManager: FlowManager
    lateinit var res: Resources
    lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        res = resources
        flowManager = FlowManager(this)
        ThemeHelper.init(this, prefs.getInt("theme", R.style.ThemeBlue))
    }
}

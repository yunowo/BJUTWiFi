package me.liuyun.bjutlgn.util

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.StyleRes
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.ui.EasterEggActivity
import me.liuyun.bjutlgn.ui.StatusLockedActivity
import java.util.*

object ThemeHelper : Application.ActivityLifecycleCallbacks {
    private val activityList = ArrayList<Activity>()
    @StyleRes
    var currentStyle: Int = 0
        private set

    fun init(application: Application, @StyleRes styleRes: Int) {
        application.registerActivityLifecycleCallbacks(this)
        this.currentStyle = styleRes
    }

    fun setTheme(activity: Activity, @StyleRes styleRes: Int) {
        currentStyle = styleRes
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putInt("theme", currentStyle).apply()
        activityList.filter { it !== activity }.forEach { it.recreate() }
        val intent = Intent(activity, activity.javaClass)
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out)
        activity.finish()
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        activityList.add(activity)
        when (activity.javaClass.simpleName) {
            StatusLockedActivity::class.java.simpleName -> activity.setTheme(R.style.AppTheme_Dialog)
            EasterEggActivity::class.java.simpleName -> activity.setTheme(android.R.style.Theme_Wallpaper_NoTitleBar)
            else -> activity.setTheme(currentStyle)
        }
    }

    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityResumed(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle?) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        activityList.remove(activity)
    }
}

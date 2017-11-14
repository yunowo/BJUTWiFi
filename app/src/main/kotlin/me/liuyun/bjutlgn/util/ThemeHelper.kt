package me.liuyun.bjutlgn.util

import android.app.Activity
import android.app.ActivityManager.TaskDescription
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.StyleRes
import android.util.TypedValue
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

    private fun getThemePrimaryColor(context: Context): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimary, value, true)
        return value.data
    }

    private fun setTaskDescription(activity: Activity) {
        val taskDescription = TaskDescription(activity.getString(R.string.app_name),
                BitmapFactory.decodeResource(activity.resources, R.mipmap.ic_launcher),
                getThemePrimaryColor(activity))
        activity.setTaskDescription(taskDescription)
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        activityList.add(activity)
        when (activity.javaClass.simpleName) {
            StatusLockedActivity::class.java.simpleName -> activity.setTheme(R.style.AppTheme_Dialog)
            EasterEggActivity::class.java.simpleName -> activity.setTheme(android.R.style.Theme_Wallpaper_NoTitleBar)
            else -> activity.setTheme(currentStyle)
        }
        setTaskDescription(activity)
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

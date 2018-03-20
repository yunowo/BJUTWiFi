package me.liuyun.bjutlgn

import android.app.Application
import android.arch.persistence.room.Room
import android.content.SharedPreferences
import android.preference.PreferenceManager
import me.liuyun.bjutlgn.db.AppDatabase
import me.liuyun.bjutlgn.util.ThemeHelper

class App : Application() {
    lateinit var appDatabase: AppDatabase
    lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        appDatabase = Room.databaseBuilder(this, AppDatabase::class.java, "bjutwifi.db").allowMainThreadQueries().build()
        ThemeHelper.init(this, ThemeHelper.getTheme(this))
    }
}

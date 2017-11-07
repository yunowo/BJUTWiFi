package me.liuyun.bjutlgn.util

import android.content.Context
import android.preference.PreferenceManager
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.entity.Stats
import java.util.regex.Pattern

object StatsUtils {
    fun parseStats(text: String): Stats {
        val stats = Stats()
        try {
            val p = Pattern.compile("time='(.*?)';flow='(.*?)';fsele=1;fee='(.*?)'")
            val m = p.matcher(text.replace(" ", ""))
            while (m.find()) {
                stats.time = m.group(1).toInt()
                stats.flow = m.group(2).toInt()
                stats.fee = m.group(3).toInt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stats.isOnline = false
        }

        return stats
    }

    fun getPack(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return context.resources.getIntArray(R.array.packages_values)[prefs.getInt("current_package", 0)]
    }

    fun getPercent(stats: Stats, context: Context) = Math.round(stats.flow.toFloat() / 1024f / 1024f / getPack(context).toFloat() * 100)
}

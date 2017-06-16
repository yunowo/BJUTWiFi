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
                stats.time = Integer.parseInt(m.group(1))
                stats.flow = Integer.parseInt(m.group(2))
                stats.fee = Integer.parseInt(m.group(3))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return stats
        //TODO if flow=0 then check real connection
    }

    fun getPack(context: Context): Int {
        val resources = context.resources
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return resources.getIntArray(R.array.packages_values)[prefs.getInt("current_package", 0)]
    }

    fun getPercent(stats: Stats, context: Context): Int {
        return Math.round(stats.flow.toFloat() / 1024f / 1024f / getPack(context).toFloat() * 100)
    }
}

package me.liuyun.bjutlgn.util

import me.liuyun.bjutlgn.App
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.entity.Stats

object StatsUtils {
    fun parseStats(text: String): Stats {
        val stats = Stats()
        try {
            val p = "time='(.*?)';flow='(.*?)';fsele=1;fee='(.*?)'".toRegex()
            p.find(text.replace(" ", ""))?.let {
                val values = it.groupValues
                stats.time = values[1].toInt()
                stats.flow = values[2].toInt()
                stats.fee = values[3].toInt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stats.isOnline = false
        }

        return stats
    }

    fun getPack(app: App) = app.resources.getIntArray(R.array.packages_values)[app.prefs.getInt("current_package", 0)]

    fun getPercent(stats: Stats, app: App) = Math.round(stats.flow.toFloat() / 1024f / 1024f / getPack(app).toFloat() * 100)
}

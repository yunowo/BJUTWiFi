package me.liuyun.bjutlgn.ui

import android.databinding.DataBindingUtil
import android.support.v7.widget.CardView
import android.util.SparseIntArray
import me.liuyun.bjutlgn.WiFiApplication
import me.liuyun.bjutlgn.databinding.GraphCardBinding
import me.liuyun.bjutlgn.entity.Flow
import me.liuyun.bjutlgn.util.StatsUtils
import java.text.DateFormat
import java.util.*

class GraphCard(cardView: CardView, private val context: WiFiApplication) {
    val binding: GraphCardBinding = DataBindingUtil.findBinding(cardView)
    var chart = binding.chart

    fun show() {
        val month = Calendar.getInstance().get(Calendar.MONTH)
        if (context.prefs.getInt("current_month", -1) != month) {
            context.prefs.edit().putInt("current_month", month).apply()
            context.flowManager.clearFlow()
        }

        val flowList = context.flowManager.allFlow ?: emptyList()
        chart.clearPaths()
        chart.configureGraph(60 * 24 * endOfCurrentMonth.get(Calendar.DATE), StatsUtils.getPack(context) * 1024, true, true)
        calcPoints(flowList)

        val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT)
        chart.setBottomLabels(arrayOf(dateFormat.format(startOfCurrentMonth.time), dateFormat.format(endOfCurrentMonth.time)))
    }

    private fun calcPoints(flowList: List<Flow>) {
        val startOfMonth = (startOfCurrentMonth.timeInMillis / 1000L).toInt() / 60
        val points = SparseIntArray()
        points.put(0, 0)

        for (flow in flowList) {
            points.put((flow.timestamp / 60 - startOfMonth).toInt(), flow.flow / 1024)
        }
        chart.addPath(points)
    }

    private val startOfCurrentMonth: Calendar
        get() {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.clear(Calendar.MINUTE)
            cal.clear(Calendar.SECOND)
            cal.clear(Calendar.MILLISECOND)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            return cal
        }

    private val endOfCurrentMonth: Calendar
        get() {
            val cal = Calendar.getInstance()
            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE))
            return cal
        }
}

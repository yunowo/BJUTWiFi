package me.liuyun.bjutlgn.ui

import android.util.SparseIntArray
import android.widget.LinearLayout
import androidx.content.edit
import com.android.settings.graph.UsageView
import kotlinx.android.synthetic.main.graph_card.view.*
import me.liuyun.bjutlgn.App
import me.liuyun.bjutlgn.entity.Flow
import me.liuyun.bjutlgn.util.StatsUtils
import java.text.DateFormat
import java.util.*

class GraphCard(cardView: LinearLayout, private val app: App) {
    val chart: UsageView = cardView.chart

    fun show() {
        val month = Calendar.getInstance().get(Calendar.MONTH)
        if (app.prefs.getInt("current_month", -1) != month) {
            app.prefs.edit { putInt("current_month", month) }
            app.appDatabase.flowDao().deleteAll()
        }

        val flowList = app.appDatabase.flowDao().all()
        chart.clearPaths()
        chart.configureGraph(60 * 24 * endOfCurrentMonth.get(Calendar.DATE), StatsUtils.getPack(app) * 1024, true, true)
        calcPoints(flowList)

        val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT)
        chart.setBottomLabels(arrayOf(dateFormat.format(startOfCurrentMonth.time), dateFormat.format(endOfCurrentMonth.time)))
    }

    private fun calcPoints(flowList: List<Flow>) {
        val startOfMonth = (startOfCurrentMonth.timeInMillis / 1000L).toInt() / 60
        val points = SparseIntArray()
        points.put(0, 0)

        flowList.forEach { points.put((it.timestamp / 60 - startOfMonth).toInt(), it.flow / 1024) }
        chart.addPath(points)
    }

    private val startOfCurrentMonth: Calendar
        get() = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            clear(Calendar.MINUTE)
            clear(Calendar.SECOND)
            clear(Calendar.MILLISECOND)
            set(Calendar.DAY_OF_MONTH, 1)
        }

    private val endOfCurrentMonth: Calendar
        get() = Calendar.getInstance().apply {
            set(Calendar.DATE, getActualMaximum(Calendar.DATE))
        }
}

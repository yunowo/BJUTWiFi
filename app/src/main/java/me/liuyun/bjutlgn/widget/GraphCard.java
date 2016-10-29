package me.liuyun.bjutlgn.widget;

import android.support.v7.widget.CardView;
import android.util.SparseIntArray;

import com.android.settingslib.graph.UsageView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.liuyun.bjutlgn.R;
import me.liuyun.bjutlgn.entity.Flow;
import me.liuyun.bjutlgn.ui.MainActivity;

public class GraphCard {
    @BindView(R.id.chart) UsageView chart;
    private MainActivity activity;
    private CardView cardView;
    private List<Flow> flowList;


    public GraphCard(CardView cardView, MainActivity activity) {
        this.cardView = cardView;
        this.activity = activity;
        ButterKnife.bind(this, cardView);
    }

    public void show() {
        int pack = activity.resources.getIntArray(R.array.packages_values)[activity.prefs.getInt("current_package", 0)];
        flowList = activity.dao.getAllFlow();
        chart.clearPaths();
        chart.configureGraph(60 * 24 * getEndOfCurrentMonth().get(Calendar.DATE), pack * 1024, true, true);
        calcPoints();

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        chart.setBottomLabels(new String[]{dateFormat.format(getStartOfCurrentMonth().getTime()),
                dateFormat.format(getEndOfCurrentMonth().getTime())});
    }

    private void calcPoints() {
        int startOfMonth = (int) (getStartOfCurrentMonth().getTimeInMillis() / 1000L) / 60;
        SparseIntArray points = new SparseIntArray();
        points.put(0, 0);
        for (Flow flow : flowList) {
            points.put((int) (flow.getTimestamp() / 60 - startOfMonth), flow.getFlow() / 1024);
        }
        if (points.size() > 1) {
            chart.addPath(points);
        }
    }

    private Calendar getStartOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal;
    }

    private Calendar getEndOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        return cal;
    }
}

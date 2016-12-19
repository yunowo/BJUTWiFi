package me.liuyun.bjutlgn.db;

import android.content.Context;
import android.util.Log;

import java.util.List;

import me.liuyun.bjutlgn.entity.Flow;

public class FlowManager {
    private static final String TAG = "FlowManager";

    private DBHelper dbHelper;

    public FlowManager(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public boolean insertFlow(long timestamp, int flow) {
        try {
            dbHelper.getFlowDao().create(new Flow(0, timestamp, flow));
            return true;
        } catch (Exception e) {
            Log.e(TAG, "insertFlow", e);
        }
        return false;
    }

    public List<Flow> getAllFlow() {
        try {
            return dbHelper.getFlowDao().queryForAll();
        } catch (Exception e) {
            Log.e(TAG, "getAllFlow", e);
        }
        return null;
    }

    public boolean clearFlow() {
        try {
            dbHelper.getFlowDao().deleteBuilder().delete();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "clearFlow", e);
        }
        return false;
    }
}

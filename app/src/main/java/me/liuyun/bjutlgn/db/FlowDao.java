package me.liuyun.bjutlgn.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.liuyun.bjutlgn.entity.Flow;

public class FlowDao {

    private static final String TAG = "FlowDao";
    private final String[] FLOW_COLUMNS = new String[]{"id", "timestamp", "flow"};
    private DBHelper dbHelper;

    public FlowDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean insertFlow(long timestamp, int flow) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("timestamp", timestamp);
            contentValues.put("flow", flow);
            db.insertOrThrow(DBHelper.TABLE_FLOW, null, contentValues);
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    public List<Flow> getAllFlow() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(DBHelper.TABLE_FLOW, FLOW_COLUMNS, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                List<Flow> StatsList = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    StatsList.add(parseStats(cursor));
                }
                return StatsList;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }

    private Flow parseStats(Cursor cursor) {
        return new Flow(cursor.getInt(cursor.getColumnIndex("id")),
                cursor.getLong(cursor.getColumnIndex("timestamp")),
                cursor.getInt(cursor.getColumnIndex("flow")));
    }
}
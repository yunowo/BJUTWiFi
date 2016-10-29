package me.liuyun.bjutlgn.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 4;
    private static final String DB_NAME = "bjutwifi.db";
    static final String TABLE_FLOW = "flow";
    static final String TABLE_USERS = "users";

    DBHelper(Context context) {
        super(context, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + DBHelper.TABLE_FLOW + " (id INTEGER PRIMARY KEY, timestamp INTEGER, flow INTEGER)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS " + DBHelper.TABLE_USERS + " (id INTEGER PRIMARY KEY, account TEXT, password TEXT, package INTEGER)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + DBHelper.TABLE_FLOW;
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS " + DBHelper.TABLE_USERS;
        db.execSQL(sql);
        onCreate(db);
    }
}

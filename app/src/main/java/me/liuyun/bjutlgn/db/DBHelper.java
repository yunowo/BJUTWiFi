package me.liuyun.bjutlgn.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import me.liuyun.bjutlgn.entity.Flow;
import me.liuyun.bjutlgn.entity.User;

public class DBHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "bjutwifi.db";
    private static final int DATABASE_VERSION = 5;

    private Dao<Flow, Integer> flowDao = null;
    private Dao<User, Integer> userDao = null;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Flow.class);
            TableUtils.createTable(connectionSource, User.class);
        } catch (SQLException e) {
            Log.e(DBHelper.class.getName(), "onCreate", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Flow.class, true);
            TableUtils.dropTable(connectionSource, User.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DBHelper.class.getName(), "onUpgrade", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<User, Integer> getUserDao() throws SQLException {
        if (userDao == null) {
            userDao = getDao(User.class);
        }
        return userDao;
    }

    public Dao<Flow, Integer> getFlowDao() throws SQLException {
        if (flowDao == null) {
            flowDao = getDao(Flow.class);
        }
        return flowDao;
    }

    @Override
    public void close() {
        super.close();
        flowDao = null;
        userDao = null;
    }
}

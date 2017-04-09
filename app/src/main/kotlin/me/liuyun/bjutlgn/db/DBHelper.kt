package me.liuyun.bjutlgn.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import me.liuyun.bjutlgn.entity.Flow
import me.liuyun.bjutlgn.entity.User
import java.sql.SQLException

class DBHelper(context: Context) : OrmLiteSqliteOpenHelper(context, DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION) {

    private var flowDao: Dao<Flow, Int>? = null
    private var userDao: Dao<User, Int>? = null

    override fun onCreate(db: SQLiteDatabase, connectionSource: ConnectionSource) {
        try {
            TableUtils.createTable(connectionSource, Flow::class.java)
            TableUtils.createTable(connectionSource, User::class.java)
        } catch (e: SQLException) {
            Log.e(DBHelper::class.java.name, "onCreate", e)
            throw RuntimeException(e)
        }

    }

    override fun onUpgrade(db: SQLiteDatabase, connectionSource: ConnectionSource, oldVersion: Int, newVersion: Int) {
        try {
            TableUtils.dropTable<Flow, Any>(connectionSource, Flow::class.java, true)
            TableUtils.dropTable<User, Any>(connectionSource, User::class.java, true)
            onCreate(db, connectionSource)
        } catch (e: SQLException) {
            Log.e(DBHelper::class.java.name, "onUpgrade", e)
            throw RuntimeException(e)
        }

    }

    @Throws(SQLException::class)
    fun getUserDao(): Dao<User, Int>? {
        if (userDao == null) {
            userDao = getDao<Dao<User, Int>, User>(User::class.java)
        }
        return userDao
    }

    @Throws(SQLException::class)
    fun getFlowDao(): Dao<Flow, Int>? {
        if (flowDao == null) {
            flowDao = getDao<Dao<Flow, Int>, Flow>(Flow::class.java)
        }
        return flowDao
    }

    override fun close() {
        super.close()
        flowDao = null
        userDao = null
    }

    companion object {
        private val DATABASE_NAME = "bjutwifi.db"
        private val DATABASE_VERSION = 5
    }
}

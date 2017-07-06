package me.liuyun.bjutlgn.db

import android.content.Context
import android.util.Log
import com.j256.ormlite.dao.Dao
import me.liuyun.bjutlgn.entity.Flow

class FlowManager(context: Context) {

    private val dbHelper: DBHelper = DBHelper(context)

    fun transaction(body: (Dao<Flow, Int>) -> Int): Int {
        try {
            return  body(dbHelper.flowDao)
        } catch (e: Exception) {
            Log.e(TAG, body.toString(), e)
        }
        return 0
    }

    fun <T> transaction(body: (Dao<Flow, Int>) -> MutableList<T>): MutableList<T> {
        try {
            return body(dbHelper.flowDao)
        } catch (e: Exception) {
            Log.e(TAG, body.toString(), e)
        }
        return mutableListOf()
    }

    fun insertFlow(timestamp: Long, flow: Int): Int {
        return transaction { it.create(Flow(0, timestamp, flow)) }
    }

    fun clearFlow(): Int {
        return transaction { it.deleteBuilder().delete() }
    }

    fun allFlow(): MutableList<Flow> {
        return transaction<Flow> { it.queryForAll() }
    }

    val TAG: String = this.javaClass.name
}

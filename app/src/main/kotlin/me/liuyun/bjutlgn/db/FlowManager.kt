package me.liuyun.bjutlgn.db

import android.content.Context
import android.util.Log
import com.j256.ormlite.dao.Dao
import me.liuyun.bjutlgn.entity.Flow

class FlowManager(context: Context) {

    private val dbHelper: DBHelper = DBHelper(context)

    fun transaction(body: (Dao<Flow, Int>) -> Unit): Boolean {
        try {
            body(dbHelper.flowDao)
            return true
        } catch (e: Exception) {
            Log.e(TAG, body.toString(), e)
        }
        return false
    }

    fun insertFlow(timestamp: Long, flow: Int): Boolean {
        return transaction { it.create(Flow(0, timestamp, flow)) }
    }

    fun clearFlow(): Boolean {
        return transaction { it.deleteBuilder().delete() }
    }

    val allFlow: List<Flow>
        get() {
            try {
                return dbHelper.flowDao.queryForAll()
            } catch (e: Exception) {
                Log.e(TAG, "getAllFlow", e)
            }

            return mutableListOf()
        }

    val TAG: String = this.javaClass.name
}

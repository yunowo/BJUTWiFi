package me.liuyun.bjutlgn.db

import android.content.Context
import android.util.Log

import me.liuyun.bjutlgn.entity.Flow

class FlowManager(context: Context) {

    private val dbHelper: DBHelper = DBHelper(context)

    fun insertFlow(timestamp: Long, flow: Int): Boolean {
        try {
            dbHelper.getFlowDao()!!.create(Flow(0, timestamp, flow))
            return true
        } catch (e: Exception) {
            Log.e(TAG, "insertFlow", e)
        }

        return false
    }

    val allFlow: List<Flow>?
        get() {
            try {
                return dbHelper.getFlowDao()!!.queryForAll()
            } catch (e: Exception) {
                Log.e(TAG, "getAllFlow", e)
            }

            return null
        }

    fun clearFlow(): Boolean {
        try {
            dbHelper.getFlowDao()!!.deleteBuilder().delete()
            return true
        } catch (e: Exception) {
            Log.e(TAG, "clearFlow", e)
        }

        return false
    }

    companion object {
        private val TAG = "FlowManager"
    }
}

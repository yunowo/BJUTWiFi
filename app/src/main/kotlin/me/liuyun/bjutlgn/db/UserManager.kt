package me.liuyun.bjutlgn.db

import android.content.Context
import android.util.Log
import com.j256.ormlite.dao.Dao

import me.liuyun.bjutlgn.entity.User

class UserManager(context: Context) {

    private val dbHelper: DBHelper = DBHelper(context)

    fun transaction(body: (Dao<User, Int>) -> Int): Int {
        try {
            return body(dbHelper.userDao)
        } catch (e: Exception) {
            Log.e(TAG, body.toString(), e)
        }
        return 0
    }

    fun <T> transaction(body: (Dao<User, Int>) -> MutableList<T>): MutableList<T> {
        try {
            return body(dbHelper.userDao)
        } catch (e: Exception) {
            Log.e(TAG, body.toString(), e)
        }
        return mutableListOf()
    }


    fun insertUser(user: User): Int {
        return transaction {
            val position = (it.queryRaw(dbHelper.userDao.queryBuilder().selectRaw("MAX(position)").prepareStatementString()).firstResult[0] ?: "-1").toInt()
            user.position = position + 1
            dbHelper.userDao.createOrUpdate(user).numLinesChanged
        }
    }

    fun updateUser(user: User): Int {
        return transaction { it.update(user) }
    }

    fun deleteUser(id: Int): Int {
        return transaction { it.deleteById(id) }
    }

    fun allUsers(): MutableList<User> {
        return transaction<User> { it.queryBuilder().orderBy("position", true).query() ?: mutableListOf() }
    }

    val TAG: String = this.javaClass.name

}

package me.liuyun.bjutlgn.db

import android.content.Context
import android.util.Log
import com.j256.ormlite.dao.Dao

import me.liuyun.bjutlgn.entity.User

class UserManager(context: Context) {

    private val dbHelper: DBHelper = DBHelper(context)

    fun transaction(body: (Dao<User, Int>) -> Unit): Boolean {
        try {
            body(dbHelper.userDao)
            return true
        } catch (e: Exception) {
            Log.e(TAG, body.toString(), e)
        }
        return false
    }

    fun insertUser(user: User): Boolean {
        return transaction {
            val position = (it.queryRaw(dbHelper.userDao.queryBuilder().selectRaw("MAX(position)").prepareStatementString()).firstResult[0] ?: "-1").toInt()
            user.position = position + 1
            dbHelper.userDao.createOrUpdate(user)
        }
    }

    fun updateUser(user: User): Boolean {
        return transaction { it.update(user) }
    }

    fun deleteUser(id: Int): Boolean {
        return transaction { it.deleteById(id) }
    }

    val allUsers: MutableList<User>
        get() {
            try {
                return dbHelper.userDao.queryBuilder().orderBy("position", true).query()
            } catch (e: Exception) {
                Log.e(TAG, "getAllUsers", e)
            }
            return mutableListOf()
        }

    val TAG: String = this.javaClass.name

}

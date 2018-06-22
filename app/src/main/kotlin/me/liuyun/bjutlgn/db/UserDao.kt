package me.liuyun.bjutlgn.db

import androidx.lifecycle.LiveData
import androidx.room.*
import me.liuyun.bjutlgn.entity.User


/**
 * Created by Yun on 2017.7.6.
 */

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY position ASC")
    fun all(): LiveData<MutableList<User>>

    @Query("SELECT * FROM users WHERE id IN (:ids)")
    fun allByIds(ids: IntArray): MutableList<User>

    @Query("SELECT * FROM users WHERE position = (SELECT max(position) FROM users) LIMIT 1")
    fun maxPosition(): User?

    @Insert
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)
}
package me.liuyun.bjutlgn.db

import android.arch.persistence.room.*
import me.liuyun.bjutlgn.entity.User


/**
 * Created by Yun on 2017.7.6.
 */

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY position ASC")
    fun all(): List<User>

    @Query("SELECT * FROM users WHERE id IN (:ids)")
    fun allByIds(ids: IntArray): List<User>

    @Query("SELECT * FROM users WHERE position = (SELECT max(position) FROM users) LIMIT 1")
    fun maxPosition(): User?

    @Insert
    fun insert(user: User)

    @Insert
    fun insertAll(vararg users: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)
}
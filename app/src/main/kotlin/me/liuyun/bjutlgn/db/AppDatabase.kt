package me.liuyun.bjutlgn.db

import androidx.room.Database
import androidx.room.RoomDatabase
import me.liuyun.bjutlgn.entity.Flow
import me.liuyun.bjutlgn.entity.User

/**
 * Created by Yun on 2017.7.6.
 */

@Database(entities = arrayOf(User::class, Flow::class), version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun flowDao(): FlowDao
}
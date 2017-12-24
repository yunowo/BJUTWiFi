package me.liuyun.bjutlgn.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

data class Stats(var flow: Int = 0, var time: Int = 0, var fee: Int = 0, var isOnline: Boolean = true)

@Entity(tableName = "flow")
data class Flow(@PrimaryKey(autoGenerate = true) var id: Int,
                var timestamp: Long,
                var flow: Int)

@Entity(tableName = "users")
data class User(@PrimaryKey(autoGenerate = true) var id: Int,
                var account: String,
                var password: String,
                var pack: Int,
                var position: Int)

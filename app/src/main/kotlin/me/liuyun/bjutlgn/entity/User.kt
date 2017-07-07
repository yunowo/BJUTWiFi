package me.liuyun.bjutlgn.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "users")
data class User(@PrimaryKey(autoGenerate = true) var id: Int,
                var account: String,
                var password: String,
                var pack: Int,
                var position: Int)

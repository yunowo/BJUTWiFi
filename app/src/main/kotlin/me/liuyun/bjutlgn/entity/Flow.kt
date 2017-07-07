package me.liuyun.bjutlgn.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "flow")
data class Flow(@PrimaryKey(autoGenerate = true) var id: Int,
                var timestamp: Long,
                var flow: Int)
package me.liuyun.bjutlgn.entity

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "flow")
data class Flow(@DatabaseField(generatedId = true) var id: Int = 0,
                @DatabaseField var timestamp: Long = 0,
                @DatabaseField var flow: Int = 0)
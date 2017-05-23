package me.liuyun.bjutlgn.entity

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "flow")
data class Flow(@DatabaseField(generatedId = true) var id: Int,
           @DatabaseField var timestamp: Long,
           @DatabaseField var flow: Int) {
    constructor() : this(0, 0, 0)
}

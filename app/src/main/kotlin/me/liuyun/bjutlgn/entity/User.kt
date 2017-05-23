package me.liuyun.bjutlgn.entity

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "users")
data class User(@DatabaseField(generatedId = true) var id: Int,
           @DatabaseField var account: String,
           @DatabaseField var password: String,
           @DatabaseField var pack: Int,
           @DatabaseField var position: Int) {
    constructor() : this(0, "", "", 0, 0)
}

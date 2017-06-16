package me.liuyun.bjutlgn.entity

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "users")
data class User(@DatabaseField(generatedId = true) var id: Int = 0,
                @DatabaseField var account: String = "",
                @DatabaseField var password: String = "",
                @DatabaseField var pack: Int = 0,
                @DatabaseField var position: Int = 0)

package com.tarlad.client.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    var id: Int,
    @Ignore
    var email: String? = null,
    @Ignore
    var password: String? = null,
    var nickname: String,
    var name: String,
    var surname: String,
    @ColumnInfo(name = "image_url")
    var imageURL: String? = null
){
    constructor(
        id: Int,
        nickname: String,
        name: String,
        surname: String,
        imageURL: String
    ) : this(id, null, null, nickname, name, surname, imageURL)
}
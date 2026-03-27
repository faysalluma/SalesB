package com.groupec.salesb.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "nomprenom")
    val nomprenom: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "password")
    val password: String,
    @ColumnInfo(name = "reset_password")
    val reset_password: String ? = null,
    @ColumnInfo(name = "reset_expires")
    val reset_expires: String ? = null,
    @ColumnInfo(name = "adresse")
    val adresse: String,
    @ColumnInfo(name = "tel")
    val tel: String,
    @ColumnInfo(name = "privilege")
    val privilege: String ? = null,
    @ColumnInfo(name = "actif")
    val actif: Boolean,
    @ColumnInfo(name = "firstlogin")
    val firstlogin: Boolean,
    @ColumnInfo(name = "datecreation")
    val datecreation: String ? = null,
    @ColumnInfo(name = "datemodif")
    val datemodif: String ? = null,
    @ColumnInfo(name = "synchronised")
    val synchronised: Boolean,
    @ColumnInfo(name = "isProActive")
    val isProActive: Boolean = false,
)

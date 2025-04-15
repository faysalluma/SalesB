package com.groupec.salesb.core.model.data

data class User(
    val id: Int,
    val nomprenom: String,
    val email: String,
    val password: String,
    val reset_password: String ? = null,
    val reset_expires: String ? = null,
    val adresse: String,
    val tel: String,
    val privilege: String,
    val actif: Boolean,
    val firstlogin: Boolean,
    val datecreation: String ? = null,
    val datemodif: String ? = null,
    val synchronised: Boolean = false
)
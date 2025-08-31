package com.groupec.salesb.core.model.data

data class UserStore(
    val id: String = "",
    val nomprenom: String = "",
    val privilege: String = "",
    val firstLogin: Boolean = false,
    val reset_password: String = ""
) {
    fun getPrivileges(): List<String> = privilege.takeIf { it.isNotEmpty() }?.toStringList() ?: listOf()
}

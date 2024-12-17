package com.groupec.salesb.core.model.data

data class UserStore(val id: String = "", val nomprenom: String = "", val privilege: String = "", val firstLogin: Boolean = false) {
    fun getPrivileges() = privilege.takeIf { it.isNotEmpty() }?.toIntList() ?: listOf()
}

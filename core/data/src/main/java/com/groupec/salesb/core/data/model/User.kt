package com.groupec.salesb.core.data.model

import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.network.model.UserItemResponse
import com.groupec.salesb.core.network.model.UserResponse
import com.groupec.salesb.core.toDate
import com.groupec.salesb.core.toDateString
import com.groupec.salesb.core.database.model.User as UserEntity


fun UserResponse.toUserList(): List<User> = users.map { it.toUser() }

fun UserItemResponse.toUser(): User {
    return User(
        id = id, nomprenom = nomprenom, email = email, password = password,
        reset_password = reset_password, reset_expires = reset_expires, adresse = adresse,
        tel = tel, privilege = privilege, actif = (actif == 1), firstlogin = (firstlogin == 1),
        datecreation = datecreation, datemodif = datemodif
    )
}

fun UserItemResponse.toUserEntity() = UserEntity(
    id = id ?: 0, nomprenom = nomprenom, email = email, password = password, adresse = adresse,
    tel = tel, privilege = privilege, actif = (actif == 1), firstlogin = (firstlogin == 1),
    datecreation = datecreation?.toDateString(), datemodif = datemodif?.toDateString(),
    reset_password = reset_password, reset_expires = reset_expires, synchronised = false
)

fun UserItemResponse.toUserStore() = UserStore(id = id.toString(), nomprenom = nomprenom, privilege = privilege ?: "",
    firstLogin = (firstlogin == 1), reset_password = reset_password ?: "")

fun UserEntity.toUser() = User(
    id = id, nomprenom = nomprenom, email = email, password = password, adresse = adresse,
    tel = tel, privilege = privilege, actif = actif, firstlogin = firstlogin,
    datecreation = datecreation?.toDate(),  datemodif = datemodif?.toDate(),  reset_password = reset_password, reset_expires = reset_expires, synchronised = synchronised)

fun UserEntity.toUserStore() = UserStore(id = id.toString(), nomprenom = nomprenom, privilege = privilege ?: "", firstLogin = firstlogin,
reset_password = reset_password ?: "")





package com.groupec.salesb.core.data.model

import com.groupec.salesb.core.currentDateString
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.network.model.UserItemResponse
import com.groupec.salesb.core.network.model.UserResponse
import com.groupec.salesb.core.database.model.User as UserEntity


fun UserItemResponse.toUser(): User {
    return User(
        id = id, nomprenom = nomprenom, email = email, password = password,
        reset_password = reset_password, reset_expires = reset_expires, adresse = adresse,
        tel = tel, privilege = privilege, actif = (actif == 1), firstlogin = (firstlogin == 1)
    )
}

fun UserItemResponse.toUserEntity() = UserEntity(
    id = id, nomprenom = nomprenom, email = email, password = password, adresse = adresse,
    tel = tel, privilege = privilege, actif = (actif == 1), firstlogin = (firstlogin == 1),
    datecreation = currentDateString(), datemodif = currentDateString(),
    reset_password = reset_password, reset_expires = reset_expires, synchronised = false
)

fun UserItemResponse.toUserStore() = UserStore(id = id.toString(), nomprenom = nomprenom, privilege = privilege,
    firstLogin = (firstlogin == 1), reset_password = reset_password ?: "")

fun UserEntity.toUser() = User(
    id = id, nomprenom = nomprenom, email = email, password = password, adresse = adresse,
    tel = tel, privilege = privilege, actif = actif, firstlogin = firstlogin,
    datecreation = datecreation,  datemodif = datemodif,  reset_password = reset_password, reset_expires = reset_expires, synchronised = synchronised)

fun UserEntity.toUserStore() = UserStore(id = id.toString(), nomprenom = nomprenom, privilege = privilege, firstLogin = firstlogin,
reset_password = reset_password ?: "")





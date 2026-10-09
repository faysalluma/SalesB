package com.groupec.salesb.core.model.data

import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
) {
    override fun toString(): String = "Login(credentials=REDACTED)"
}

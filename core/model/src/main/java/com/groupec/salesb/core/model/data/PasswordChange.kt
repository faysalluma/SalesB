package com.groupec.salesb.core.model.data

import com.google.gson.annotations.SerializedName

data class PasswordChange(
    @SerializedName("email") val email: String,
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("new_password") val newPassword: String
) {
    override fun toString(): String = "PasswordChange(credentials=REDACTED)"
}

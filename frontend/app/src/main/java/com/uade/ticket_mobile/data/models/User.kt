package com.uade.ticket_mobile.data.models

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val username: String,
    val email: String,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("is_staff")
    val isStaff: Boolean = false,
    @SerializedName("is_superuser")
    val isSuperuser: Boolean = false
)

data class UserLoginRequest(
    val username: String,
    val password: String
)

data class UserLoginResponse(
    val access: String,
    val refresh: String,
    val user: User
)

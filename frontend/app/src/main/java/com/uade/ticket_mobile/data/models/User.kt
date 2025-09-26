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

data class UserRegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String
)

data class PasswordResetRequest(
    val email: String
)

data class UpdateProfileRequest(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val email: String
)

data class ChangePasswordRequest(
    @SerializedName("old_password")
    val oldPassword: String,
    @SerializedName("new_password")
    val newPassword: String
)
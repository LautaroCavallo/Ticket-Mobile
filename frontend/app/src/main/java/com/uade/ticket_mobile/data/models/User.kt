package com.uade.ticket_mobile.data.models

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val username: String? = null,
    val email: String,
    @SerializedName("firstName")
    val firstName: String?,
    @SerializedName("lastName")
    val lastName: String?,
    @SerializedName("isStaff")
    val isStaff: Boolean = false,
    @SerializedName("isSuperuser")
    val isSuperuser: Boolean = false,
    val role: String? = null
)

data class UserLoginRequest(
    val email: String,
    val password: String
)

data class UserLoginResponse(
    @SerializedName("accessToken")
    val access: String,
    @SerializedName("refreshToken")
    val refresh: String,
    val user: User
)

data class UserRegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String
)

data class PasswordResetRequest(
    val email: String
)

data class UpdateProfileRequest(
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    val email: String
)

data class ChangePasswordRequest(
    @SerializedName("currentPassword")
    val currentPassword: String,
    @SerializedName("newPassword")
    val newPassword: String
)
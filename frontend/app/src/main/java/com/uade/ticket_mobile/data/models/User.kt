package com.uade.ticket_mobile.data.models

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val username: String? = null,
    val email: String,
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("fullName")
    val fullName: String? = null,
    @SerializedName("displayRole")
    val displayRole: String? = null,
    @SerializedName("isStaff")
    val isStaff: Boolean = false,
    @SerializedName("isSuperuser")
    val isSuperuser: Boolean = false,
    @SerializedName("isActive")
    val isActive: Boolean = true,
    val role: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("lastLogin")
    val lastLogin: String? = null
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

data class UserRoleUpdateRequest(
    val role: String
)

data class UserRoleUpdateResponse(
    val msg: String? = null,
    val user: User
)

data class UserActivationRequest(
    @SerializedName("isActive")
    val isActive: Boolean
)

data class UserActivationResponse(
    val msg: String? = null,
    val user: User
)
package com.uade.ticket_mobile.data.api

import com.uade.ticket_mobile.data.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Authentication
    @POST("auth/login/")
    suspend fun login(@Body request: UserLoginRequest): Response<UserLoginResponse>
    
    @POST("auth/register/")
    suspend fun register(@Body request: UserRegisterRequest): Response<UserLoginResponse>
    
    @POST("auth/password-reset/")
    suspend fun requestPasswordReset(@Body request: PasswordResetRequest): Response<Unit>
    
    @POST("auth/refresh/")
    suspend fun refreshToken(@Body refresh: String): Response<TokenResponse>
    
    @GET("auth/user/")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<User>
    
    @PUT("auth/user/")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<User>
    
    @POST("auth/change-password/")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<Unit>
    
    // Tickets
    @GET("tickets/")
    suspend fun getTickets(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null,
        @Query("priority") priority: String? = null,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<PagedResponse<Ticket>>
    
    @GET("tickets/{id}/")
    suspend fun getTicket(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Ticket>
    
    @Multipart
    @POST("tickets/create/")
    suspend fun createTicket(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("priority") priority: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): Response<TicketCreateResponse>
    
    @PUT("tickets/{id}/update/")
    suspend fun updateTicket(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: TicketUpdateRequest
    ): Response<Ticket>
    
    @DELETE("tickets/{id}/delete/")
    suspend fun deleteTicket(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
    
    // Categories
    @GET("categories/")
    suspend fun getCategories(
        @Header("Authorization") token: String
    ): Response<List<TicketCategory>>
    
    // Users
    @GET("users/support/")
    suspend fun getSupportUsers(
        @Header("Authorization") token: String
    ): Response<PagedResponse<User>>
    
    @GET("users/")
    suspend fun getUsers(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("role") role: String? = null,
        @Query("is_active") isActive: String? = null,
        @Query("search") search: String? = null
    ): Response<PagedResponse<User>>
    
    @GET("users/{id}/")
    suspend fun getUserDetail(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Response<User>
    
    @PATCH("users/{id}/role/")
    suspend fun updateUserRole(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body request: UserRoleUpdateRequest
    ): Response<UserRoleUpdateResponse>
    
    @PATCH("users/{id}/activation/")
    suspend fun toggleUserActivation(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body request: UserActivationRequest
    ): Response<UserActivationResponse>
    
    @DELETE("users/{id}/delete/")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Response<Unit>
    
    // Attachments
    @GET("tickets/{ticket_id}/attachments/")
    suspend fun getTicketAttachments(
        @Header("Authorization") token: String,
        @Path("ticket_id") ticketId: Int
    ): Response<AttachmentListResponse>
    
    @Multipart
    @POST("tickets/{ticket_id}/attachments/upload/")
    suspend fun uploadAttachment(
        @Header("Authorization") token: String,
        @Path("ticket_id") ticketId: Int,
        @Part file: MultipartBody.Part,
        @Part("isPrivate") isPrivate: RequestBody? = null
    ): Response<AttachmentUploadResponse>
    
    @DELETE("tickets/{ticket_id}/attachments/{attachment_id}/delete/")
    suspend fun deleteAttachment(
        @Header("Authorization") token: String,
        @Path("ticket_id") ticketId: Int,
        @Path("attachment_id") attachmentId: Int
    ): Response<Unit>
    
    // Metrics
    @GET("metrics/tickets/overview/")
    suspend fun getTicketsOverview(
        @Header("Authorization") token: String
    ): Response<TicketsOverviewResponse>
    
    @GET("metrics/tickets/performance/")
    suspend fun getTicketsPerformance(
        @Header("Authorization") token: String
    ): Response<TicketsPerformanceResponse>
    
    @GET("metrics/users/activity/")
    suspend fun getUsersActivity(
        @Header("Authorization") token: String
    ): Response<UsersActivityResponse>
    
    @GET("metrics/system/health/")
    suspend fun getSystemHealth(
        @Header("Authorization") token: String
    ): Response<SystemHealthResponse>
    
    // Comments
    @GET("tickets/{ticket_id}/comments/")
    suspend fun getTicketComments(
        @Header("Authorization") token: String,
        @Path("ticket_id") ticketId: Int
    ): Response<CommentListResponse>
    
    @POST("tickets/{ticket_id}/comments/create/")
    suspend fun createComment(
        @Header("Authorization") token: String,
        @Path("ticket_id") ticketId: Int,
        @Body request: CommentCreateRequest
    ): Response<CommentCreateResponse>
    
    @DELETE("tickets/{ticket_id}/comments/{comment_id}/delete/")
    suspend fun deleteComment(
        @Header("Authorization") token: String,
        @Path("ticket_id") ticketId: Int,
        @Path("comment_id") commentId: Int
    ): Response<Unit>
}

data class TokenResponse(
    val access: String
)


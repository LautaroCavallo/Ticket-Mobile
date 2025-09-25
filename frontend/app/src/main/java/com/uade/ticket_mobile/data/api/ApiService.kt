package com.uade.ticket_mobile.data.api

import com.uade.ticket_mobile.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Authentication
    @POST("auth/login/")
    suspend fun login(@Body request: UserLoginRequest): Response<UserLoginResponse>
    
    @POST("auth/refresh/")
    suspend fun refreshToken(@Body refresh: String): Response<TokenResponse>
    
    @GET("auth/user/")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<User>
    
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
    
    @POST("tickets/")
    suspend fun createTicket(
        @Header("Authorization") token: String,
        @Body request: TicketCreateRequest
    ): Response<Ticket>
    
    @PUT("tickets/{id}/")
    suspend fun updateTicket(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: TicketUpdateRequest
    ): Response<Ticket>
    
    @DELETE("tickets/{id}/")
    suspend fun deleteTicket(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
    
    // Categories
    @GET("categories/")
    suspend fun getCategories(
        @Header("Authorization") token: String
    ): Response<List<TicketCategory>>
}

data class TokenResponse(
    val access: String
)


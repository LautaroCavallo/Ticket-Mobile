package com.uade.ticket_mobile.data.repository

import android.content.Context
import com.uade.ticket_mobile.data.api.ApiClient
import com.uade.ticket_mobile.data.local.AppDatabase
import com.uade.ticket_mobile.data.local.entities.TicketEntity
import com.uade.ticket_mobile.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response

class TicketRepository(context: Context? = null) {
    private val apiService = ApiClient.apiService
    private val ticketDao = context?.let { AppDatabase.getDatabase(it).ticketDao() }
    
    suspend fun login(username: String, password: String): Response<UserLoginResponse> {
        return apiService.login(UserLoginRequest(username, password))
    }
    
    suspend fun register(firstName: String, lastName: String, email: String, password: String): Response<UserLoginResponse> {
        return apiService.register(
            UserRegisterRequest(
                username = email,
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName
            )
        )
    }
    
    suspend fun requestPasswordReset(email: String): Response<Unit> {
        return apiService.requestPasswordReset(PasswordResetRequest(email))
    }
    
    suspend fun updateProfile(token: String, firstName: String, lastName: String, email: String): Response<User> {
        return apiService.updateProfile(
            "Bearer $token",
            UpdateProfileRequest(firstName, lastName, email)
        )
    }
    
    suspend fun changePassword(token: String, currentPassword: String, newPassword: String): Response<Unit> {
        return apiService.changePassword(
            "Bearer $token",
            ChangePasswordRequest(
                currentPassword = currentPassword,
                newPassword = newPassword
            )
        )
    }
    
    suspend fun getCurrentUser(token: String): Response<User> {
        return apiService.getCurrentUser("Bearer $token")
    }
    
    suspend fun getTickets(
        token: String,
        status: String? = null,
        priority: String? = null,
        page: Int = 1
    ): Response<PagedResponse<Ticket>> {
        return try {
            val response = apiService.getTickets("Bearer $token", status, priority, page)
            
            // Si la respuesta es exitosa, guardar en caché
            if (response.isSuccessful && response.body() != null) {
                val tickets = response.body()!!.results
                ticketDao?.insertTickets(tickets.map { TicketEntity.fromTicket(it) })
            }
            
            response
        } catch (e: Exception) {
            // Si falla la conexión, intentar cargar desde caché
            response
        }
    }
    
    /**
     * Get tickets from local cache (offline mode)
     */
    fun getTicketsFromCache(): Flow<List<Ticket>>? {
        return ticketDao?.getAllTickets()?.map { entities ->
            entities.map { it.toTicket() }
        }
    }
    
    /**
     * Get tickets by status from cache
     */
    fun getTicketsFromCacheByStatus(status: String): Flow<List<Ticket>>? {
        return ticketDao?.getTicketsByStatus(status)?.map { entities ->
            entities.map { it.toTicket() }
        }
    }
    
    suspend fun getTicket(token: String, id: Int): Response<Ticket> {
        return apiService.getTicket("Bearer $token", id)
    }
    
    suspend fun createTicket(token: String, request: TicketCreateRequest): Response<Ticket> {
        val response = apiService.createTicket("Bearer $token", request)
        
        // Guardar en caché si fue exitoso
        if (response.isSuccessful && response.body() != null) {
            ticketDao?.insertTicket(TicketEntity.fromTicket(response.body()!!))
        }
        
        return response
    }
    
    suspend fun updateTicket(token: String, id: Int, request: TicketUpdateRequest): Response<Ticket> {
        return apiService.updateTicket("Bearer $token", id, request)
    }
    
    suspend fun deleteTicket(token: String, id: Int): Response<Unit> {
        val response = apiService.deleteTicket("Bearer $token", id)
        
        // Eliminar de caché si fue exitoso
        if (response.isSuccessful) {
            ticketDao?.deleteTicketById(id)
        }
        
        return response
    }
    
    /**
     * Clear all cached tickets
     */
    suspend fun clearCache() {
        ticketDao?.deleteAllTickets()
    }
    
    suspend fun getCategories(token: String): Response<List<TicketCategory>> {
        return apiService.getCategories("Bearer $token")
    }
    
    // Metrics methods
    suspend fun getTicketsOverview(token: String): Response<TicketsOverviewResponse> {
        return apiService.getTicketsOverview("Bearer $token")
    }
    
    suspend fun getTicketsPerformance(token: String): Response<TicketsPerformanceResponse> {
        return apiService.getTicketsPerformance("Bearer $token")
    }
    
    suspend fun getUsersActivity(token: String): Response<UsersActivityResponse> {
        return apiService.getUsersActivity("Bearer $token")
    }
    
    suspend fun getSystemHealth(token: String): Response<SystemHealthResponse> {
        return apiService.getSystemHealth("Bearer $token")
    }
}

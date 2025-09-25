package com.uade.ticket_mobile.data.repository

import com.uade.ticket_mobile.data.api.ApiClient
import com.uade.ticket_mobile.data.models.*
import retrofit2.Response

class TicketRepository {
    private val apiService = ApiClient.apiService
    
    suspend fun login(username: String, password: String): Response<UserLoginResponse> {
        return apiService.login(UserLoginRequest(username, password))
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
        return apiService.getTickets("Bearer $token", status, priority, page)
    }
    
    suspend fun getTicket(token: String, id: Int): Response<Ticket> {
        return apiService.getTicket("Bearer $token", id)
    }
    
    suspend fun createTicket(token: String, request: TicketCreateRequest): Response<Ticket> {
        return apiService.createTicket("Bearer $token", request)
    }
    
    suspend fun updateTicket(token: String, id: Int, request: TicketUpdateRequest): Response<Ticket> {
        return apiService.updateTicket("Bearer $token", id, request)
    }
    
    suspend fun deleteTicket(token: String, id: Int): Response<Unit> {
        return apiService.deleteTicket("Bearer $token", id)
    }
    
    suspend fun getCategories(token: String): Response<List<TicketCategory>> {
        return apiService.getCategories("Bearer $token")
    }
}

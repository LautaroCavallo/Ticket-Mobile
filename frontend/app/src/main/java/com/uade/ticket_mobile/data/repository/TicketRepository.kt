package com.uade.ticket_mobile.data.repository

import android.content.Context
import com.uade.ticket_mobile.data.api.ApiClient
import com.uade.ticket_mobile.data.local.AppDatabase
import com.uade.ticket_mobile.data.local.entities.TicketEntity
import com.uade.ticket_mobile.data.models.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.io.File

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
        val response = apiService.getTickets("Bearer $token", status, priority, page)
        
        // Si la respuesta es exitosa, guardar en caché
        if (response.isSuccessful && response.body() != null) {
            val tickets = response.body()!!.results
            ticketDao?.insertTickets(tickets.map { TicketEntity.fromTicket(it) })
        }
        
        return response
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
    
    suspend fun createTicket(
        token: String,
        title: String,
        description: String,
        priority: String,
        imageFile: File? = null
    ): Response<Ticket> {
        // Crear RequestBody para los campos de texto
        val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val priorityBody = priority.toRequestBody("text/plain".toMediaTypeOrNull())
        
        // Crear MultipartBody.Part para la imagen (si existe)
        val imagePart = imageFile?.let {
            val requestFile = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", it.name, requestFile)
        }
        
        val response = apiService.createTicket(
            "Bearer $token",
            titleBody,
            descriptionBody,
            priorityBody,
            imagePart
        )
        
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
    
    suspend fun getSupportUsers(token: String): Response<PagedResponse<User>> {
        return apiService.getSupportUsers("Bearer $token")
    }
    
    // Attachments
    suspend fun getTicketAttachments(token: String, ticketId: Int): Response<AttachmentListResponse> {
        return apiService.getTicketAttachments("Bearer $token", ticketId)
    }
    
    suspend fun uploadAttachment(
        token: String,
        ticketId: Int,
        file: File,
        isPrivate: Boolean = false
    ): Response<AttachmentUploadResponse> {
        // Detectar MIME type basado en la extensión del archivo
        val mimeType = when (file.extension.lowercase()) {
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "txt" -> "text/plain"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "bmp" -> "image/bmp"
            "zip" -> "application/zip"
            "rar" -> "application/x-rar-compressed"
            "7z" -> "application/x-7z-compressed"
            else -> "application/octet-stream"
        }
        
        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val isPrivateBody = isPrivate.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        
        return apiService.uploadAttachment("Bearer $token", ticketId, filePart, isPrivateBody)
    }
    
    suspend fun deleteAttachment(token: String, ticketId: Int, attachmentId: Int): Response<Unit> {
        return apiService.deleteAttachment("Bearer $token", ticketId, attachmentId)
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

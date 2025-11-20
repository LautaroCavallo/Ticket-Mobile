package com.uade.ticket_mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uade.ticket_mobile.data.models.*
import com.uade.ticket_mobile.data.repository.TicketRepository
import com.uade.ticket_mobile.utils.AnalyticsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class TicketViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TicketRepository(application)
    private val analytics = AnalyticsManager(application)
    
    private val _uiState = MutableStateFlow(TicketUiState())
    val uiState: StateFlow<TicketUiState> = _uiState.asStateFlow()
    
    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    val tickets: StateFlow<List<Ticket>> = _tickets.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = repository.login(username, password)
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            accessToken = it.access,
                            refreshToken = it.refresh
                        )
                        _currentUser.value = it.user
                        loadTickets()
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = response.message() ?: "Error de autenticación"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }
    
    fun loadTickets() {
        val token = _uiState.value.accessToken ?: "mock_token"
        viewModelScope.launch {
            try {
                val response = repository.getTickets(token)
                if (response.isSuccessful) {
                    _tickets.value = response.body()?.results ?: emptyList()
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = response.message() ?: "Error al cargar tickets"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }
    
    fun createTicket(title: String, description: String, priority: String, imageFile: File? = null) {
        val token = _uiState.value.accessToken ?: "mock_token"
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, ticketCreatedSuccessfully = false)
            try {
                val response = repository.createTicket(token, title, description, priority, imageFile)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        ticketCreatedSuccessfully = true
                    )
                    loadTickets() // Recargar la lista
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = response.message() ?: "Error al crear ticket"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun resetTicketCreated() {
        _uiState.value = _uiState.value.copy(ticketCreatedSuccessfully = false)
    }
    
    fun register(firstName: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = repository.register(firstName, lastName, email, password)
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    registerResponse?.let {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            accessToken = it.access,
                            refreshToken = it.refresh
                        )
                        _currentUser.value = it.user
                        loadTickets()
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = response.message() ?: "Error en el registro"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }
    
    fun requestPasswordReset(email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = repository.requestPasswordReset(email)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = response.message() ?: "Error al solicitar recuperación"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }
    
    fun updateProfile(firstName: String, lastName: String, email: String) {
        val token = _uiState.value.accessToken ?: "mock_token"
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = repository.updateProfile(token, firstName, lastName, email)
                if (response.isSuccessful) {
                    val updatedUser = response.body()
                    updatedUser?.let {
                        _currentUser.value = it
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = response.message() ?: "Error al actualizar perfil"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }
    
    fun changePassword(currentPassword: String, newPassword: String) {
        val token = _uiState.value.accessToken ?: "mock_token"
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = repository.changePassword(token, currentPassword, newPassword)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = response.message() ?: "Error al cambiar contraseña"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }
    
    fun getTicketsByStatus(status: TicketStatus): List<Ticket> {
        return _tickets.value.filter { it.safeStatus == status }
    }
    
    // Users
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()
    
    private val _loadingUsers = MutableStateFlow(false)
    val loadingUsers: StateFlow<Boolean> = _loadingUsers.asStateFlow()
    
    private val _usersError = MutableStateFlow<String?>(null)
    val usersError: StateFlow<String?> = _usersError.asStateFlow()
    
    fun setUsersError(error: String?) {
        _usersError.value = error
    }
    
    fun loadUsers(
        role: String? = null,
        isActive: Boolean? = null,
        search: String? = null
    ) {
        val token = _uiState.value.accessToken
        if (token == null) {
            println("ERROR: No hay token de acceso")
            return
        }
        viewModelScope.launch {
            _loadingUsers.value = true
            _usersError.value = null // Limpiar error previo
            try {
                println("DEBUG: Attempting to load users with token: ${token.take(20)}...")
                val response = repository.getUsers(token, role = role, isActive = isActive, search = search)
                println("DEBUG: Response code: ${response.code()}")
                println("DEBUG: Response isSuccessful: ${response.isSuccessful}")
                println("DEBUG: Response message: ${response.message()}")
                println("DEBUG: Current user role: ${_currentUser.value?.role}")
                println("DEBUG: Current user isSuperuser: ${_currentUser.value?.isSuperuser}")
                println("DEBUG: Has response body: ${response.body() != null}")
                
                if (response.isSuccessful) {
                    val body = response.body()
                    println("DEBUG: Response body is null: ${body == null}")
                    if (body != null) {
                        println("DEBUG: Response count: ${body.count}")
                        println("DEBUG: Response next: ${body.next}")
                        println("DEBUG: Response previous: ${body.previous}")
                        println("DEBUG: Results count: ${body.results.size}")
                        if (body.results.isNotEmpty()) {
                            println("DEBUG: First user ID: ${body.results[0].id}")
                            println("DEBUG: First user email: ${body.results[0].email}")
                            println("DEBUG: First user role: ${body.results[0].role}")
                            println("DEBUG: First user full object: ${body.results[0]}")
                        } else {
                            println("WARNING: Response body is not null but results list is empty!")
                            println("DEBUG: Full response body: $body")
                        }
                        _users.value = body.results
                        _usersError.value = null // Limpiar error si fue exitoso
                    } else {
                        println("ERROR: Response body is null")
                        _users.value = emptyList()
                        _usersError.value = "Error: La respuesta del servidor está vacía"
                    }
                } else {
                    val errorBody = try {
                        response.errorBody()?.string()
                    } catch (e: Exception) {
                        null
                    }
                    println("ERROR: Failed to load users - Code: ${response.code()}, Message: ${response.message()}")
                    println("ERROR: Error body: $errorBody")
                    _users.value = emptyList()
                    _usersError.value = when (response.code()) {
                        403 -> "No tienes permisos para ver usuarios. Se requiere rol de administrador (sysAdmin). Tu rol actual: ${_currentUser.value?.role ?: "desconocido"}"
                        401 -> "Sesión expirada. Por favor, inicia sesión nuevamente."
                        404 -> "Endpoint no encontrado. Verifica la URL del servidor."
                        else -> "Error al cargar usuarios (${response.code()}): ${response.message()}. ${errorBody?.take(100) ?: ""}"
                    }
                }
            } catch (e: Exception) {
                println("ERROR: Exception loading users: ${e.message}")
                println("ERROR: Exception type: ${e.javaClass.simpleName}")
                e.printStackTrace()
                _users.value = emptyList()
                _usersError.value = "Error de conexión: ${e.message}"
                if (e.message?.contains("Json") == true || e.message?.contains("parse") == true) {
                    _usersError.value = "Error al procesar la respuesta del servidor. Verifica el formato de datos."
                }
            } finally {
                _loadingUsers.value = false
            }
        }
    }
    
    fun loadSupportUsers() {
        val token = _uiState.value.accessToken ?: return
        viewModelScope.launch {
            try {
                val response = repository.getSupportUsers(token)
                if (response.isSuccessful) {
                    _users.value = response.body()?.results ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun deleteUser(
        userId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val token = _uiState.value.accessToken ?: return
        viewModelScope.launch {
            try {
                val response = repository.deleteUser(token, userId)
                if (response.isSuccessful) {
                    loadUsers() // Recargar lista
                    onSuccess()
                } else {
                    onError(response.message() ?: "Error al eliminar usuario")
                }
            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
            }
        }
    }
    
    fun updateUserRole(
        userId: Int,
        role: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val token = _uiState.value.accessToken ?: return
        viewModelScope.launch {
            try {
                val response = repository.updateUserRole(token, userId, role)
                if (response.isSuccessful) {
                    loadUsers() // Recargar lista
                    onSuccess()
                } else {
                    onError(response.message() ?: "Error al actualizar rol")
                }
            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
            }
        }
    }
    
    fun toggleUserActivation(
        userId: Int,
        isActive: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val token = _uiState.value.accessToken ?: return
        viewModelScope.launch {
            try {
                val response = repository.toggleUserActivation(token, userId, isActive)
                if (response.isSuccessful) {
                    loadUsers() // Recargar lista
                    onSuccess()
                } else {
                    onError(response.message() ?: "Error al cambiar estado del usuario")
                }
            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
            }
        }
    }
    
    fun updateTicket(ticketId: Int, request: TicketUpdateRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val token = _uiState.value.accessToken ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = repository.updateTicket(token, ticketId, request)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    loadTickets()
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = response.message() ?: "Error al actualizar ticket"
                    )
                    onError(response.message() ?: "Error al actualizar ticket")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.message}"
                )
                onError("Error de conexión: ${e.message}")
            }
        }
    }
    
    // Attachments
    private val _attachments = MutableStateFlow<List<Attachment>>(emptyList())
    val attachments: StateFlow<List<Attachment>> = _attachments.asStateFlow()
    
    private val _uploadingAttachment = MutableStateFlow(false)
    val uploadingAttachment: StateFlow<Boolean> = _uploadingAttachment.asStateFlow()
    
    fun loadAttachments(ticketId: Int) {
        val token = _uiState.value.accessToken ?: return
        viewModelScope.launch {
            try {
                val response = repository.getTicketAttachments(token, ticketId)
                if (response.isSuccessful) {
                    _attachments.value = response.body()?.attachments ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun uploadAttachment(ticketId: Int, file: File, isPrivate: Boolean = false, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val token = _uiState.value.accessToken ?: return
        viewModelScope.launch {
            _uploadingAttachment.value = true
            try {
                val response = repository.uploadAttachment(token, ticketId, file, isPrivate)
                if (response.isSuccessful) {
                    loadAttachments(ticketId)
                    onSuccess()
                } else {
                    onError(response.message() ?: "Error al subir archivo")
                }
            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
            } finally {
                _uploadingAttachment.value = false
            }
        }
    }
    
    fun deleteAttachment(ticketId: Int, attachmentId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val token = _uiState.value.accessToken ?: return
        viewModelScope.launch {
            try {
                val response = repository.deleteAttachment(token, ticketId, attachmentId)
                if (response.isSuccessful) {
                    loadAttachments(ticketId)
                    onSuccess()
                } else {
                    onError(response.message() ?: "Error al eliminar archivo")
                }
            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
            }
        }
    }
    
    fun logout() {
        _uiState.value = TicketUiState()
        _tickets.value = emptyList()
        _currentUser.value = null
        _attachments.value = emptyList()
        _comments.value = emptyList()
    }
    
    // Comments
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()
    
    private val _loadingComments = MutableStateFlow(false)
    val loadingComments: StateFlow<Boolean> = _loadingComments.asStateFlow()
    
    fun loadComments(ticketId: Int) {
        val token = _uiState.value.accessToken ?: return
        viewModelScope.launch {
            _loadingComments.value = true
            try {
                val response = repository.getTicketComments(token, ticketId)
                if (response.isSuccessful) {
                    _comments.value = response.body()?.comments ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loadingComments.value = false
            }
        }
    }
    
    fun createComment(
        ticketId: Int,
        text: String,
        isPrivate: Boolean = false,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val token = _uiState.value.accessToken ?: return
        viewModelScope.launch {
            try {
                val response = repository.createComment(token, ticketId, text, isPrivate)
                if (response.isSuccessful) {
                    loadComments(ticketId) // Recargar comentarios
                    onSuccess()
                } else {
                    onError(response.message() ?: "Error al crear comentario")
                }
            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
            }
        }
    }
    
    fun deleteComment(
        ticketId: Int,
        commentId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val token = _uiState.value.accessToken ?: return
        viewModelScope.launch {
            try {
                val response = repository.deleteComment(token, ticketId, commentId)
                if (response.isSuccessful) {
                    loadComments(ticketId) // Recargar comentarios
                    onSuccess()
                } else {
                    onError(response.message() ?: "Error al eliminar comentario")
                }
            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
            }
        }
    }
}

data class TicketUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val error: String? = null,
    val ticketCreatedSuccessfully: Boolean = false
)

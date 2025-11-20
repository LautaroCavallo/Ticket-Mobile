package com.uade.ticket_mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uade.ticket_mobile.data.models.*
import com.uade.ticket_mobile.data.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TicketViewModel : ViewModel() {
    private val repository = TicketRepository()
    
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
    
    fun createTicket(title: String, description: String, priority: String) {
        val token = _uiState.value.accessToken ?: "mock_token"
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, ticketCreatedSuccessfully = false)
            try {
                val request = TicketCreateRequest(title, description, priority)
                val response = repository.createTicket(token, request)
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
        return _tickets.value.filter { it.status == status }
    }
    
    fun logout() {
        _uiState.value = TicketUiState()
        _tickets.value = emptyList()
        _currentUser.value = null
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

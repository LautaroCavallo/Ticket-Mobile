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
                        error = "Error de autenticación: ${response.code()}"
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
        val token = _uiState.value.accessToken ?: return
        viewModelScope.launch {
            try {
                val response = repository.getTickets(token)
                if (response.isSuccessful) {
                    _tickets.value = response.body()?.results ?: emptyList()
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Error al cargar tickets: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }
    
    fun createTicket(title: String, description: String, priority: String, categoryId: Int?) {
        val token = _uiState.value.accessToken ?: return
        viewModelScope.launch {
            try {
                val request = TicketCreateRequest(title, description, priority, categoryId)
                val response = repository.createTicket(token, request)
                if (response.isSuccessful) {
                    loadTickets() // Recargar la lista
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Error al crear ticket: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
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
    val error: String? = null
)

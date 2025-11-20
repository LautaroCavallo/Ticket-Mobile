package com.uade.ticket_mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uade.ticket_mobile.data.models.*
import com.uade.ticket_mobile.data.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MetricsViewModel : ViewModel() {
    private val repository = TicketRepository()
    
    private val _ticketsOverview = MutableStateFlow<TicketsOverviewResponse?>(null)
    val ticketsOverview: StateFlow<TicketsOverviewResponse?> = _ticketsOverview.asStateFlow()
    
    private val _ticketsPerformance = MutableStateFlow<TicketsPerformanceResponse?>(null)
    val ticketsPerformance: StateFlow<TicketsPerformanceResponse?> = _ticketsPerformance.asStateFlow()
    
    private val _usersActivity = MutableStateFlow<UsersActivityResponse?>(null)
    val usersActivity: StateFlow<UsersActivityResponse?> = _usersActivity.asStateFlow()
    
    private val _systemHealth = MutableStateFlow<SystemHealthResponse?>(null)
    val systemHealth: StateFlow<SystemHealthResponse?> = _systemHealth.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadTicketsOverview(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = repository.getTicketsOverview(token)
                if (response.isSuccessful) {
                    _ticketsOverview.value = response.body()
                } else {
                    _error.value = "Error al cargar métricas de tickets"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadTicketsPerformance(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = repository.getTicketsPerformance(token)
                if (response.isSuccessful) {
                    _ticketsPerformance.value = response.body()
                } else {
                    _error.value = "Error al cargar métricas de rendimiento"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadUsersActivity(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = repository.getUsersActivity(token)
                if (response.isSuccessful) {
                    _usersActivity.value = response.body()
                } else {
                    _error.value = "Error al cargar actividad de usuarios"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadSystemHealth(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = repository.getSystemHealth(token)
                if (response.isSuccessful) {
                    _systemHealth.value = response.body()
                } else {
                    _error.value = "Error al cargar salud del sistema"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadAllMetrics(token: String) {
        loadTicketsOverview(token)
        loadTicketsPerformance(token)
        loadSystemHealth(token)
        // loadUsersActivity solo si es admin
    }
    
    fun clearError() {
        _error.value = null
    }
}


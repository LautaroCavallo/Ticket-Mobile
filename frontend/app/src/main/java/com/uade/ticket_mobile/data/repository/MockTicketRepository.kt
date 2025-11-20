package com.uade.ticket_mobile.data.repository

import com.uade.ticket_mobile.data.models.*
import com.uade.ticket_mobile.data.mock.MockData
import kotlinx.coroutines.delay
import retrofit2.Response
import retrofit2.http.Body
import java.util.Date

class MockTicketRepository {
    
    private var nextTicketId = 9
    private val tickets = MockData.mockTickets.toMutableList()
    private var currentUser = MockData.mockUser
    
    suspend fun login(username: String, password: String): MockResponse<UserLoginResponse> {
        // Simular delay de red
        delay(1000)
        
        val validPassword = MockData.validCredentials[username]
        return if (validPassword == password) {
            val user = when (username) {
                "admin@test.com" -> MockData.mockAdminUser
                else -> MockData.mockUser.copy(username = username, email = username)
            }
            currentUser = user
            
            MockResponse.success(
                UserLoginResponse(
                    access = "mock_access_token_${System.currentTimeMillis()}",
                    refresh = "mock_refresh_token_${System.currentTimeMillis()}",
                    user = user
                )
            )
        } else {
            MockResponse.error("Credenciales inválidas")
        }
    }
    
    suspend fun register(
        firstName: String, 
        lastName: String, 
        email: String, 
        password: String
    ): MockResponse<UserLoginResponse> {
        delay(1500) // Simular delay más largo para registro
        
        // Verificar si el email ya existe
        if (MockData.validCredentials.containsKey(email)) {
            return MockResponse.error("El email ya está registrado")
        }
        
        val newUser = User(
            id = 999, // ID temporal para mock
            username = email,
            email = email,
            firstName = firstName,
            lastName = lastName,
            isStaff = false,
            isSuperuser = false
        )
        
        currentUser = newUser
        
        return MockResponse.success(
            UserLoginResponse(
                access = "mock_access_token_${System.currentTimeMillis()}",
                refresh = "mock_refresh_token_${System.currentTimeMillis()}",
                user = newUser
            )
        )
    }
    
    suspend fun requestPasswordReset(email: String): MockResponse<Unit> {
        delay(800)
        // Simular que siempre funciona para cualquier email
        return MockResponse.success(Unit)
    }
    
    suspend fun updateProfile(
        token: String, 
        firstName: String, 
        lastName: String, 
        email: String
    ): MockResponse<User> {
        delay(1000)
        
        currentUser = currentUser.copy(
            firstName = firstName,
            lastName = lastName,
            email = email,
            username = email
        )
        
        return MockResponse.success(currentUser)
    }
    
    suspend fun changePassword(
        token: String, 
        currentPassword: String, 
        newPassword: String
    ): MockResponse<Unit> {
        delay(1200)
        
        // Simular validación de contraseña actual
        val validPassword = MockData.validCredentials[currentUser.email]
        return if (validPassword == currentPassword) {
            MockResponse.success(Unit)
        } else {
            MockResponse.error("Contraseña actual incorrecta")
        }
    }
    
    suspend fun getCurrentUser(token: String): MockResponse<User> {
        delay(500)
        return MockResponse.success(currentUser)
    }
    
    suspend fun getTickets(
        token: String,
        status: String? = null,
        priority: String? = null,
        page: Int = 1
    ): MockResponse<PagedResponse<Ticket>> {
        delay(800)
        
        var filteredTickets = tickets.toList()
        
        // Filtrar por estado si se especifica
        status?.let { statusFilter ->
            try {
                val ticketStatus = TicketStatus.valueOf(statusFilter.uppercase())
                filteredTickets = filteredTickets.filter { it.status == ticketStatus }
            } catch (e: IllegalArgumentException) {
                // Ignorar filtro de estado inválido
            }
        }
        
        // Filtrar por prioridad si se especifica
        priority?.let { priorityFilter ->
            try {
                val ticketPriority = TicketPriority.valueOf(priorityFilter.uppercase())
                filteredTickets = filteredTickets.filter { it.priority == ticketPriority }
            } catch (e: IllegalArgumentException) {
                // Ignorar filtro de prioridad inválido
            }
        }
        
        return MockResponse.success(
            PagedResponse(
                count = filteredTickets.size,
                next = null,
                previous = null,
                results = filteredTickets
            )
        )
    }
    
    suspend fun getTicket(token: String, id: Int): MockResponse<Ticket> {
        delay(400)
        
        val ticket = tickets.find { it.id == id }
        return if (ticket != null) {
            MockResponse.success(ticket)
        } else {
            MockResponse.error("Ticket no encontrado")
        }
    }
    
    suspend fun createTicket(
        token: String, 
        request: TicketCreateRequest
    ): MockResponse<Ticket> {
        delay(1000)
        
        val priority = try {
            TicketPriority.valueOf(request.priority.uppercase())
        } catch (e: IllegalArgumentException) {
            TicketPriority.MEDIUM
        }
        
        val newTicket = Ticket(
            id = nextTicketId++,
            title = request.title,
            description = request.description,
            status = TicketStatus.OPEN,
            priority = priority,
            createdAt = Date().toString(),
            updatedAt = Date().toString(),
            creator = currentUser,
            assignee = null
        )
        
        tickets.add(0, newTicket) // Agregar al inicio de la lista
        
        return MockResponse.success(newTicket)
    }
    
    suspend fun updateTicket(
        token: String, 
        id: Int, 
        request: TicketUpdateRequest
    ): MockResponse<Ticket> {
        delay(800)
        
        val ticketIndex = tickets.indexOfFirst { it.id == id }
        if (ticketIndex == -1) {
            return MockResponse.error("Ticket no encontrado")
        }
        
        val originalTicket = tickets[ticketIndex]
        val updatedTicket = originalTicket.copy(
            title = request.title ?: originalTicket.title,
            description = request.description ?: originalTicket.description,
            status = request.status?.let { 
                try { TicketStatus.valueOf(it.uppercase()) } 
                catch (e: IllegalArgumentException) { originalTicket.status } 
            } ?: originalTicket.status,
            priority = request.priority?.let { 
                try { TicketPriority.valueOf(it.uppercase()) } 
                catch (e: IllegalArgumentException) { originalTicket.priority } 
            } ?: originalTicket.priority,
            updatedAt = Date().toString()
        )
        
        tickets[ticketIndex] = updatedTicket
        
        return MockResponse.success(updatedTicket)
    }
    
    suspend fun deleteTicket(token: String, id: Int): MockResponse<Unit> {
        delay(600)
        
        val removed = tickets.removeIf { it.id == id }
        return if (removed) {
            MockResponse.success(Unit)
        } else {
            MockResponse.error("Ticket no encontrado")
        }
    }
    
    suspend fun getCategories(token: String): MockResponse<List<TicketCategory>> {
        delay(300)
        return MockResponse.success(MockData.mockCategories)
    }
}

// Clase para simular Response de Retrofit
data class MockResponse<T>(
    val body: T?,
    val isSuccessful: Boolean,
    val code: Int,
    val message: String?
) {
    companion object {
        fun <T> success(body: T): MockResponse<T> {
            return MockResponse(body, true, 200, null)
        }
        
        fun <T> error(message: String, code: Int = 400): MockResponse<T> {
            return MockResponse(null, false, code, message)
        }
    }
}

// Función de extensión para convertir MockResponse a Response (para compatibilidad)
fun <T> MockResponse<T>.toRetrofitResponse(): Response<T> {
    return if (isSuccessful && body != null) {
        Response.success(body)
    } else {
        Response.error(code, okhttp3.ResponseBody.create(null, message ?: "Error"))
    }
}

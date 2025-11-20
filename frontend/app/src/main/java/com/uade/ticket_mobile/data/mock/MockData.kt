package com.uade.ticket_mobile.data.mock

import com.uade.ticket_mobile.data.models.*

object MockData {
    
    // Usuario mock para login
    val mockUser = User(
        id = 1,
        username = "usuario@test.com",
        email = "usuario@test.com",
        firstName = "Juan",
        lastName = "Pérez",
        isStaff = false,
        isSuperuser = false
    )
    
    val mockAdminUser = User(
        id = 2,
        username = "admin@test.com",
        email = "admin@test.com",
        firstName = "Admin",
        lastName = "Sistema",
        isStaff = true,
        isSuperuser = true
    )
    
    // Categorías mock
    val mockCategories = listOf(
        TicketCategory(1, "RRHH", "Recursos Humanos"),
        TicketCategory(2, "IT", "Tecnología"),
        TicketCategory(3, "Administración", "Administración General"),
        TicketCategory(4, "Mantenimiento", "Mantenimiento de instalaciones")
    )
    
    // Tickets mock
    val mockTickets = listOf(
        Ticket(
            id = 1,
            title = "Password reset not working",
            description = "Can't reset my password :(",
            status = TicketStatus.OPEN,
            priority = TicketPriority.HIGH,
            createdAt = "2024-03-23T10:30:00Z",
            updatedAt = "2024-03-23T10:30:00Z",
            creator = mockUser,
            assignee = null
        ),
        Ticket(
            id = 2,
            title = "Error en sistema de nóminas",
            description = "El sistema de nóminas no está calculando correctamente las horas extras del mes actual",
            status = TicketStatus.IN_PROGRESS,
            priority = TicketPriority.URGENT,
            createdAt = "2024-03-22T14:15:00Z",
            updatedAt = "2024-03-23T09:00:00Z",
            creator = mockUser,
            assignee = mockAdminUser
        ),
        Ticket(
            id = 3,
            title = "Solicitud de vacaciones",
            description = "Necesito solicitar vacaciones para la primera semana de abril",
            status = TicketStatus.RESOLVED,
            priority = TicketPriority.LOW,
            createdAt = "2024-03-20T11:00:00Z",
            updatedAt = "2024-03-21T16:30:00Z",
            creator = mockUser,
            assignee = mockAdminUser
        ),
        Ticket(
            id = 4,
            title = "Problema con impresora de oficina",
            description = "La impresora del segundo piso no está funcionando correctamente, se atasca constantemente",
            status = TicketStatus.OPEN,
            priority = TicketPriority.MEDIUM,
            createdAt = "2024-03-23T08:45:00Z",
            updatedAt = "2024-03-23T08:45:00Z",
            creator = mockUser,
            assignee = null
        ),
        Ticket(
            id = 5,
            title = "Acceso al sistema ERP",
            description = "No puedo acceder al sistema ERP desde hace dos días",
            status = TicketStatus.CLOSED,
            priority = TicketPriority.HIGH,
            createdAt = "2024-03-18T13:20:00Z",
            updatedAt = "2024-03-19T10:15:00Z",
            creator = mockUser,
            assignee = mockAdminUser
        ),
        Ticket(
            id = 6,
            title = "Actualización de datos personales",
            description = "Necesito actualizar mi dirección y teléfono en el sistema",
            status = TicketStatus.RESOLVED,
            priority = TicketPriority.LOW,
            createdAt = "2024-03-21T15:30:00Z",
            updatedAt = "2024-03-22T11:45:00Z",
            creator = mockUser,
            assignee = mockAdminUser
        ),
        Ticket(
            id = 7,
            title = "Error en carga de documentos",
            description = "El sistema no me permite subir documentos PDF, aparece un error 500",
            status = TicketStatus.CLOSED,
            priority = TicketPriority.HIGH,
            createdAt = "2024-03-17T09:15:00Z",
            updatedAt = "2024-03-18T14:20:00Z",
            creator = mockUser,
            assignee = mockAdminUser
        ),
        Ticket(
            id = 8,
            title = "Mantenimiento aire acondicionado",
            description = "El aire acondicionado de la sala de reuniones hace ruido",
            status = TicketStatus.IN_PROGRESS,
            priority = TicketPriority.LOW,
            createdAt = "2024-03-22T16:00:00Z",
            updatedAt = "2024-03-23T08:30:00Z",
            creator = mockUser,
            assignee = null
        )
    )
    
    // Credenciales válidas para login mock
    val validCredentials = mapOf(
        "usuario@test.com" to "123456",
        "admin@test.com" to "admin123",
        "test@test.com" to "test123"
    )
    
    fun getTicketsByStatus(status: TicketStatus): List<Ticket> {
        return mockTickets.filter { it.status == status }
    }
    
    fun getTicketsForPending(): List<Ticket> {
        return mockTickets.filter { 
            it.status == TicketStatus.OPEN || it.status == TicketStatus.IN_PROGRESS 
        }
    }
    
    fun getTicketsForCompleted(): List<Ticket> {
        return mockTickets.filter { it.status == TicketStatus.RESOLVED }
    }
    
    fun getTicketsForCanceled(): List<Ticket> {
        return mockTickets.filter { it.status == TicketStatus.CLOSED || it.status == TicketStatus.CANCELED }
    }
    
    fun getUnassignedTickets(): List<Ticket> {
        return mockTickets.filter { it.assignee == null }
    }
    
    // Usuarios adicionales para gestión de admin
    val allUsers = listOf(
        mockUser,
        mockAdminUser,
        User(
            id = 3,
            username = "maria.garcia@test.com",
            email = "maria.garcia@test.com",
            firstName = "María",
            lastName = "García",
            isStaff = true,
            isSuperuser = false
        ),
        User(
            id = 4,
            username = "laura.sola@test.com",
            email = "laura.sola@test.com",
            firstName = "Laura",
            lastName = "Sola",
            isStaff = false,
            isSuperuser = false
        )
    )
    
    // Comentarios mock para tickets
    data class TicketComment(
        val id: Int,
        val ticketId: Int,
        val author: User,
        val content: String,
        val isPrivate: Boolean,
        val createdAt: String
    )
    
    val mockComments = listOf(
        TicketComment(
            id = 1,
            ticketId = 1,
            author = mockAdminUser,
            content = "He revisado el ticket y estoy trabajando en la solución.",
            isPrivate = false,
            createdAt = "2024-03-23T11:00:00Z"
        ),
        TicketComment(
            id = 2,
            ticketId = 1,
            author = mockUser,
            content = "Gracias por la respuesta. ¿Hay algún tiempo estimado?",
            isPrivate = false,
            createdAt = "2024-03-23T11:30:00Z"
        ),
        TicketComment(
            id = 3,
            ticketId = 1,
            author = mockAdminUser,
            content = "Nota interna: Revisar configuración del servidor de email",
            isPrivate = true,
            createdAt = "2024-03-23T12:00:00Z"
        )
    )
    
    // Estadísticas mock
    data class TicketStatistics(
        val totalTickets: Int,
        val openTickets: Int,
        val inProgressTickets: Int,
        val resolvedTickets: Int,
        val closedTickets: Int,
        val highPriorityTickets: Int,
        val mediumPriorityTickets: Int,
        val lowPriorityTickets: Int,
        val urgentPriorityTickets: Int
    )
    
    fun getTicketStatistics(): TicketStatistics {
        return TicketStatistics(
            totalTickets = mockTickets.size,
            openTickets = mockTickets.count { it.status == TicketStatus.OPEN },
            inProgressTickets = mockTickets.count { it.status == TicketStatus.IN_PROGRESS },
            resolvedTickets = mockTickets.count { it.status == TicketStatus.RESOLVED },
            closedTickets = mockTickets.count { it.status == TicketStatus.CLOSED },
            highPriorityTickets = mockTickets.count { it.priority == TicketPriority.HIGH },
            mediumPriorityTickets = mockTickets.count { it.priority == TicketPriority.MEDIUM },
            lowPriorityTickets = mockTickets.count { it.priority == TicketPriority.LOW },
            urgentPriorityTickets = mockTickets.count { it.priority == TicketPriority.URGENT }
        )
    }
}

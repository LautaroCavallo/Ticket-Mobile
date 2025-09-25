package com.uade.ticket_mobile.data.models

import com.google.gson.annotations.SerializedName

data class Ticket(
    val id: Int,
    val title: String,
    val description: String,
    val status: TicketStatus,
    val priority: TicketPriority,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val creator: User,
    val assignee: User?,
    val category: TicketCategory?
)

enum class TicketStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}

enum class TicketPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

data class TicketCategory(
    val id: Int,
    val name: String,
    val description: String?
)

data class TicketCreateRequest(
    val title: String,
    val description: String,
    val priority: String,
    val category: Int?
)

data class TicketUpdateRequest(
    val title: String? = null,
    val description: String? = null,
    val status: String? = null,
    val priority: String? = null,
    val assignee: Int? = null,
    val category: Int? = null
)

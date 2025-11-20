package com.uade.ticket_mobile.data.models

import com.google.gson.annotations.SerializedName

data class Ticket(
    val id: Int,
    val title: String,
    val description: String?,
    val status: TicketStatus,
    val priority: TicketPriority,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    val creator: User,
    val assignee: User?,
    @SerializedName("imageUrl")
    val imageUrl: String? = null
)

enum class TicketStatus {
    @SerializedName("open")
    OPEN,
    @SerializedName("in_progress")
    IN_PROGRESS,
    @SerializedName("resolved")
    RESOLVED,
    @SerializedName("closed")
    CLOSED,
    @SerializedName("canceled")
    CANCELED
}

enum class TicketPriority {
    @SerializedName("low")
    LOW,
    @SerializedName("medium")
    MEDIUM,
    @SerializedName("high")
    HIGH,
    @SerializedName("urgent")
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
    val priority: String
)

data class TicketUpdateRequest(
    val title: String? = null,
    val description: String? = null,
    val status: String? = null,
    val priority: String? = null,
    @SerializedName("assigneeId")
    val assigneeId: Int? = null
)

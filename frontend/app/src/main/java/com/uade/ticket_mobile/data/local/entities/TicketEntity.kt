package com.uade.ticket_mobile.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uade.ticket_mobile.data.models.Ticket
import com.uade.ticket_mobile.data.models.TicketCategory
import com.uade.ticket_mobile.data.models.TicketPriority
import com.uade.ticket_mobile.data.models.TicketStatus
import com.uade.ticket_mobile.data.models.User

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String,
    val status: String,
    val priority: String,
    val createdAt: String,
    val updatedAt: String,
    val creatorId: Int,
    val creatorUsername: String,
    val creatorEmail: String,
    val creatorFirstName: String?,
    val creatorLastName: String?,
    val assigneeId: Int?,
    val assigneeUsername: String?,
    val assigneeEmail: String?,
    val assigneeFirstName: String?,
    val assigneeLastName: String?,
    val categoryId: Int?,
    val categoryName: String?,
    val categoryDescription: String?,
    val image: String?
) {
    fun toTicket(): Ticket {
        return Ticket(
            id = id,
            title = title,
            description = description,
            status = TicketStatus.valueOf(status.uppercase().replace(" ", "_")),
            priority = TicketPriority.valueOf(priority.uppercase()),
            createdAt = createdAt,
            updatedAt = updatedAt,
            creator = User(
                id = creatorId,
                username = creatorUsername,
                email = creatorEmail,
                firstName = creatorFirstName,
                lastName = creatorLastName,
                isStaff = false,
                isSuperuser = false
            ),
            assignee = assigneeId?.let {
                User(
                    id = it,
                    username = assigneeUsername ?: "",
                    email = assigneeEmail ?: "",
                    firstName = assigneeFirstName,
                    lastName = assigneeLastName,
                    isStaff = false,
                    isSuperuser = false
                )
            },
            category = categoryId?.let {
                TicketCategory(
                    id = it,
                    name = categoryName ?: "",
                    description = categoryDescription
                )
            },
            image = image
        )
    }
    
    companion object {
        fun fromTicket(ticket: Ticket): TicketEntity {
            return TicketEntity(
                id = ticket.id,
                title = ticket.title,
                description = ticket.description,
                status = ticket.status.name,
                priority = ticket.priority.name,
                createdAt = ticket.createdAt,
                updatedAt = ticket.updatedAt,
                creatorId = ticket.creator.id,
                creatorUsername = ticket.creator.username,
                creatorEmail = ticket.creator.email,
                creatorFirstName = ticket.creator.firstName,
                creatorLastName = ticket.creator.lastName,
                assigneeId = ticket.assignee?.id,
                assigneeUsername = ticket.assignee?.username,
                assigneeEmail = ticket.assignee?.email,
                assigneeFirstName = ticket.assignee?.firstName,
                assigneeLastName = ticket.assignee?.lastName,
                categoryId = ticket.category?.id,
                categoryName = ticket.category?.name,
                categoryDescription = ticket.category?.description,
                image = ticket.image
            )
        }
    }
}


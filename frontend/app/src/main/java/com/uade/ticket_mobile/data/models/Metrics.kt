package com.uade.ticket_mobile.data.models

import com.google.gson.annotations.SerializedName

/**
 * Response model for tickets overview metrics
 */
data class TicketsOverviewResponse(
    val statusMetrics: StatusMetrics,
    val priorityMetrics: PriorityMetrics
)

data class StatusMetrics(
    val total: Int,
    val open: Int,
    @SerializedName("inProgress")
    val inProgress: Int,
    val resolved: Int,
    val closed: Int,
    val canceled: Int,
    val unassigned: Int
)

data class PriorityMetrics(
    val low: Int,
    val medium: Int,
    val high: Int,
    val urgent: Int
)

/**
 * Response model for tickets performance metrics
 */
data class TicketsPerformanceResponse(
    val averageResolutionTime: Float,
    val totalResolved: Int,
    val totalCreatedToday: Int,
    val totalCreatedThisWeek: Int,
    val totalCreatedThisMonth: Int,
    val resolutionRate: Float
)

/**
 * Response model for users activity metrics
 */
data class UsersActivityResponse(
    val count: Int,
    val users: List<UserActivity>
)

data class UserActivity(
    val userId: Int,
    val userName: String,
    val email: String,
    val role: String,
    val ticketsCreated: Int,
    val ticketsAssigned: Int,
    val ticketsResolved: Int,
    val commentsPosted: Int,
    val lastActivity: String
)

/**
 * Response model for system health metrics
 */
data class SystemHealthResponse(
    val status: String, // "healthy", "warning", "critical"
    val totalUsers: Int,
    val activeUsers: Int,
    val totalTickets: Int,
    val openTickets: Int,
    val urgentTickets: Int,
    val unassignedTickets: Int,
    val averageResponseTime: Float
)


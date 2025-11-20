package com.uade.ticket_mobile.utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class AnalyticsManager(context: Context) {
    private val firebaseAnalytics: FirebaseAnalytics? = try {
        Firebase.analytics
    } catch (e: Exception) {
        Log.w("AnalyticsManager", "Firebase Analytics no disponible: ${e.message}")
        null
    }
    
    private val TAG = "AnalyticsManager"
    
    // User events
    fun logLogin(method: String) {
        firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.LOGIN) {
            param(FirebaseAnalytics.Param.METHOD, method)
        } ?: Log.d(TAG, "Login event: method=$method")
    }
    
    fun logSignUp(method: String) {
        firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SIGN_UP) {
            param(FirebaseAnalytics.Param.METHOD, method)
        } ?: Log.d(TAG, "SignUp event: method=$method")
    }
    
    // Ticket events
    fun logTicketCreated(priority: String, category: String?) {
        firebaseAnalytics?.logEvent("ticket_created") {
            param("priority", priority)
            param("category", category ?: "none")
        } ?: Log.d(TAG, "TicketCreated event: priority=$priority, category=$category")
    }
    
    fun logTicketViewed(ticketId: Int, priority: String) {
        firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.VIEW_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, ticketId.toString())
            param("priority", priority)
        } ?: Log.d(TAG, "TicketViewed event: ticketId=$ticketId, priority=$priority")
    }
    
    fun logTicketUpdated(ticketId: Int, status: String) {
        firebaseAnalytics?.logEvent("ticket_updated") {
            param("ticket_id", ticketId.toString())
            param("new_status", status)
        } ?: Log.d(TAG, "TicketUpdated event: ticketId=$ticketId, status=$status")
    }
    
    fun logTicketDeleted(ticketId: Int) {
        firebaseAnalytics?.logEvent("ticket_deleted") {
            param("ticket_id", ticketId.toString())
        } ?: Log.d(TAG, "TicketDeleted event: ticketId=$ticketId")
    }
    
    // Comment events
    fun logCommentAdded(ticketId: Int) {
        firebaseAnalytics?.logEvent("comment_added") {
            param("ticket_id", ticketId.toString())
        } ?: Log.d(TAG, "CommentAdded event: ticketId=$ticketId")
    }
    
    // Screen view events
    fun logScreenView(screenName: String) {
        firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        } ?: Log.d(TAG, "ScreenView event: screenName=$screenName")
    }
    
    // Metrics events
    fun logMetricsViewed(tab: String) {
        firebaseAnalytics?.logEvent("metrics_viewed") {
            param("tab_name", tab)
        } ?: Log.d(TAG, "MetricsViewed event: tab=$tab")
    }
    
    // Search events
    fun logSearch(searchTerm: String, resultsCount: Int) {
        firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SEARCH) {
            param(FirebaseAnalytics.Param.SEARCH_TERM, searchTerm)
            param("results_count", resultsCount.toString())
        } ?: Log.d(TAG, "Search event: searchTerm=$searchTerm, resultsCount=$resultsCount")
    }
    
    // User properties
    fun setUserRole(role: String) {
        firebaseAnalytics?.setUserProperty("user_role", role)
        Log.d(TAG, "UserRole set: role=$role")
    }
    
    fun setUserId(userId: String) {
        firebaseAnalytics?.setUserId(userId)
        Log.d(TAG, "UserId set: userId=$userId")
    }
    
    // Error events
    fun logError(errorType: String, errorMessage: String) {
        firebaseAnalytics?.logEvent("error_occurred") {
            param("error_type", errorType)
            param("error_message", errorMessage)
        }
        Log.e(TAG, "Error event: type=$errorType, message=$errorMessage")
    }
    
    // Onboarding events
    fun logOnboardingCompleted() {
        firebaseAnalytics?.logEvent("onboarding_completed") {
            param("timestamp", System.currentTimeMillis().toString())
        } ?: Log.d(TAG, "OnboardingCompleted event")
    }
    
    fun logOnboardingSkipped() {
        firebaseAnalytics?.logEvent("onboarding_skipped") {
            param("timestamp", System.currentTimeMillis().toString())
        } ?: Log.d(TAG, "OnboardingSkipped event")
    }
}

// Extension function for easier event creation
private inline fun FirebaseAnalytics.logEvent(
    name: String,
    block: Bundle.() -> Unit
) {
    try {
        val bundle = Bundle().apply(block)
        this.logEvent(name, bundle)
    } catch (e: Exception) {
        Log.w("AnalyticsManager", "Error logging event $name: ${e.message}")
    }
}

private fun Bundle.param(key: String, value: String) {
    putString(key, value)
}


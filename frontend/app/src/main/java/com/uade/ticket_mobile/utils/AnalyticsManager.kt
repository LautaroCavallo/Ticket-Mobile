package com.uade.ticket_mobile.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class AnalyticsManager(context: Context) {
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
    
    // User events
    fun logLogin(method: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
            param(FirebaseAnalytics.Param.METHOD, method)
        }
    }
    
    fun logSignUp(method: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP) {
            param(FirebaseAnalytics.Param.METHOD, method)
        }
    }
    
    // Ticket events
    fun logTicketCreated(priority: String, category: String?) {
        firebaseAnalytics.logEvent("ticket_created") {
            param("priority", priority)
            param("category", category ?: "none")
        }
    }
    
    fun logTicketViewed(ticketId: Int, priority: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, ticketId.toString())
            param("priority", priority)
        }
    }
    
    fun logTicketUpdated(ticketId: Int, status: String) {
        firebaseAnalytics.logEvent("ticket_updated") {
            param("ticket_id", ticketId.toString())
            param("new_status", status)
        }
    }
    
    fun logTicketDeleted(ticketId: Int) {
        firebaseAnalytics.logEvent("ticket_deleted") {
            param("ticket_id", ticketId.toString())
        }
    }
    
    // Comment events
    fun logCommentAdded(ticketId: Int) {
        firebaseAnalytics.logEvent("comment_added") {
            param("ticket_id", ticketId.toString())
        }
    }
    
    // Screen view events
    fun logScreenView(screenName: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
    }
    
    // Metrics events
    fun logMetricsViewed(tab: String) {
        firebaseAnalytics.logEvent("metrics_viewed") {
            param("tab_name", tab)
        }
    }
    
    // Search events
    fun logSearch(searchTerm: String, resultsCount: Int) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH) {
            param(FirebaseAnalytics.Param.SEARCH_TERM, searchTerm)
            param("results_count", resultsCount.toString())
        }
    }
    
    // User properties
    fun setUserRole(role: String) {
        firebaseAnalytics.setUserProperty("user_role", role)
    }
    
    fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
    }
    
    // Error events
    fun logError(errorType: String, errorMessage: String) {
        firebaseAnalytics.logEvent("error_occurred") {
            param("error_type", errorType)
            param("error_message", errorMessage)
        }
    }
    
    // Onboarding events
    fun logOnboardingCompleted() {
        firebaseAnalytics.logEvent("onboarding_completed") {
            param("timestamp", System.currentTimeMillis().toString())
        }
    }
    
    fun logOnboardingSkipped() {
        firebaseAnalytics.logEvent("onboarding_skipped") {
            param("timestamp", System.currentTimeMillis().toString())
        }
    }
}

// Extension function for easier event creation
private inline fun FirebaseAnalytics.logEvent(
    name: String,
    block: Bundle.() -> Unit
) {
    val bundle = Bundle().apply(block)
    this.logEvent(name, bundle)
}

private fun Bundle.param(key: String, value: String) {
    putString(key, value)
}


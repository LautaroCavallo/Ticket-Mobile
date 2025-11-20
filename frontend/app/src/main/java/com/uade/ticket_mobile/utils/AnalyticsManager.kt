package com.uade.ticket_mobile.utils

import android.content.Context
import android.os.Bundle
import android.util.Log

class AnalyticsManager(context: Context) {
    // Firebase Analytics deshabilitado temporalmente
    // Descomentar cuando se configure Firebase
    // private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
    
    private val TAG = "AnalyticsManager"
    
    // User events
    fun logLogin(method: String) {
        Log.d(TAG, "Login event: method=$method")
        // firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
        //     param(FirebaseAnalytics.Param.METHOD, method)
        // }
    }
    
    fun logSignUp(method: String) {
        Log.d(TAG, "SignUp event: method=$method")
    }
    
    // Ticket events
    fun logTicketCreated(priority: String, category: String?) {
        Log.d(TAG, "TicketCreated event: priority=$priority, category=$category")
    }
    
    fun logTicketViewed(ticketId: Int, priority: String) {
        Log.d(TAG, "TicketViewed event: ticketId=$ticketId, priority=$priority")
    }
    
    fun logTicketUpdated(ticketId: Int, status: String) {
        Log.d(TAG, "TicketUpdated event: ticketId=$ticketId, status=$status")
    }
    
    fun logTicketDeleted(ticketId: Int) {
        Log.d(TAG, "TicketDeleted event: ticketId=$ticketId")
    }
    
    // Comment events
    fun logCommentAdded(ticketId: Int) {
        Log.d(TAG, "CommentAdded event: ticketId=$ticketId")
    }
    
    // Screen view events
    fun logScreenView(screenName: String) {
        Log.d(TAG, "ScreenView event: screenName=$screenName")
    }
    
    // Metrics events
    fun logMetricsViewed(tab: String) {
        Log.d(TAG, "MetricsViewed event: tab=$tab")
    }
    
    // Search events
    fun logSearch(searchTerm: String, resultsCount: Int) {
        Log.d(TAG, "Search event: searchTerm=$searchTerm, resultsCount=$resultsCount")
    }
    
    // User properties
    fun setUserRole(role: String) {
        Log.d(TAG, "UserRole set: role=$role")
    }
    
    fun setUserId(userId: String) {
        Log.d(TAG, "UserId set: userId=$userId")
    }
    
    // Error events
    fun logError(errorType: String, errorMessage: String) {
        Log.e(TAG, "Error event: type=$errorType, message=$errorMessage")
    }
    
    // Onboarding events
    fun logOnboardingCompleted() {
        Log.d(TAG, "OnboardingCompleted event")
    }
    
    fun logOnboardingSkipped() {
        Log.d(TAG, "OnboardingSkipped event")
    }
}


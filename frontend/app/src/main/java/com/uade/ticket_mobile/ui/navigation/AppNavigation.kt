package com.uade.ticket_mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uade.ticket_mobile.ui.screens.LoginScreen
import com.uade.ticket_mobile.ui.screens.TicketListScreen
import com.uade.ticket_mobile.ui.viewmodel.TicketViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: TicketViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = if (uiState.isAuthenticated) "ticket_list" else "login"
    ) {
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("ticket_list") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        composable("ticket_list") {
            TicketListScreen(
                viewModel = viewModel,
                onCreateTicket = {
                    // TODO: Navigate to create ticket screen
                },
                onLogout = {
                    viewModel.logout()
                    navController.navigate("login") {
                        popUpTo("ticket_list") { inclusive = true }
                    }
                }
            )
        }
    }
}

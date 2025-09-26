package com.uade.ticket_mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uade.ticket_mobile.ui.screens.MockInfoScreen
import com.uade.ticket_mobile.ui.screens.LoginScreen
import com.uade.ticket_mobile.ui.screens.RegisterScreen
import com.uade.ticket_mobile.ui.screens.ForgotPasswordScreen
import com.uade.ticket_mobile.ui.screens.TicketListScreen
import com.uade.ticket_mobile.ui.screens.CreateTicketScreen
import com.uade.ticket_mobile.ui.screens.ProfileScreen
import com.uade.ticket_mobile.ui.screens.ChangePasswordScreen
import com.uade.ticket_mobile.ui.viewmodel.TicketViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: TicketViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Para mockup, iniciamos con información del demo
    NavHost(
        navController = navController,
        startDestination = "mock_info"
    ) {
        composable("mock_info") {
            MockInfoScreen(
                onContinue = {
                    navController.navigate("login") {
                        popUpTo("mock_info") { inclusive = true }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("ticket_list") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onNavigateToForgotPassword = {
                    navController.navigate("forgot_password")
                }
            )
        }
        
        composable("register") {
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate("ticket_list") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }
        
        composable("forgot_password") {
            ForgotPasswordScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("forgot_password") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        
        composable("ticket_list") {
            TicketListScreen(
                viewModel = viewModel,
                onCreateTicket = {
                    navController.navigate("create_ticket")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                },
                onLogout = {
                    viewModel.logout()
                    navController.navigate("login") {
                        popUpTo("ticket_list") { inclusive = true }
                    }
                }
            )
        }
        
        composable("create_ticket") {
            CreateTicketScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTicketCreated = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("profile") {
            ProfileScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToChangePassword = {
                    navController.navigate("change_password")
                }
            )
        }
        
        composable("change_password") {
            ChangePasswordScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPasswordChanged = {
                    navController.popBackStack()
                }
            )
        }
    }
}

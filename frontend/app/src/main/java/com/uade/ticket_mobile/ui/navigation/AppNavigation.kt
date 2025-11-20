package com.uade.ticket_mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uade.ticket_mobile.ui.screens.*
import com.uade.ticket_mobile.ui.viewmodel.TicketViewModel
import com.uade.ticket_mobile.utils.PreferencesManager

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: TicketViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context) }
    
    // Estado reactivo para el onboarding
    var isOnboardingCompleted by remember { mutableStateOf(prefsManager.isOnboardingCompleted()) }
    
    // Determinar pantalla inicial
    val startDestination = if (isOnboardingCompleted) {
        "login"
    } else {
        "onboarding"
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding (primera vez)
        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    prefsManager.setOnboardingCompleted(true)
                    isOnboardingCompleted = true
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
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
            val currentUser by viewModel.currentUser.collectAsState()
            
            if (currentUser?.isStaff == true) {
                // Mostrar pantalla de admin si es staff
                AdminHomeScreen(
                    viewModel = viewModel,
                    onNavigateToUserManagement = {
                        navController.navigate("user_management")
                    },
                    onNavigateToStatistics = {
                        navController.navigate("statistics")
                    },
                    onNavigateToProfile = {
                        navController.navigate("profile")
                    },
                    onTicketClick = { ticket ->
                        navController.navigate("ticket_details/${ticket.id}")
                    },
                    onLogout = {
                        viewModel.logout()
                        navController.navigate("login") {
                            popUpTo("ticket_list") { inclusive = true }
                        }
                    }
                )
            } else {
                // Mostrar pantalla normal de usuario
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
        
        // Rutas de administrador
        composable("user_management") {
            UserManagementScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("statistics") {
            StatisticsScreen(
                ticketViewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("ticket_details/{ticketId}") { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId")?.toIntOrNull() ?: 1
            val tickets by viewModel.tickets.collectAsState()
            val ticket = tickets.find { it.id == ticketId } ?: tickets.first()
            
            TicketDetailsScreen(
                ticket = ticket,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditTicket = { ticketToEdit ->
                    navController.navigate("edit_ticket/${ticketToEdit.id}")
                },
                viewModel = viewModel
            )
        }
        
        composable("edit_ticket/{ticketId}") { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId")?.toIntOrNull() ?: 1
            val tickets by viewModel.tickets.collectAsState()
            val ticket = tickets.find { it.id == ticketId } ?: tickets.first()
            
            EditTicketScreen(
                ticket = ticket,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaveChanges = { updatedTicket ->
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }
    }
}

package com.uade.ticket_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uade.ticket_mobile.data.models.Ticket
import com.uade.ticket_mobile.data.models.TicketPriority
import com.uade.ticket_mobile.data.models.TicketStatus
import com.uade.ticket_mobile.ui.theme.ErrorRed
import com.uade.ticket_mobile.ui.theme.AccentOrange
import com.uade.ticket_mobile.ui.theme.SuccessGreen
import com.uade.ticket_mobile.ui.viewmodel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    viewModel: TicketViewModel = viewModel(),
    onNavigateToUserManagement: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onTicketClick: (Ticket) -> Unit,
    onLogout: () -> Unit
) {
    val tickets by viewModel.tickets.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }
    
    val tabTitles = listOf("Pendientes", "Completados", "Cancelados")
    
    LaunchedEffect(Unit) {
        viewModel.loadTickets()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home Admin") },
                navigationIcon = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Usuario actual
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    // Avatar y nombre del admin
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.tertiary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.firstName?.firstOrNull()?.toString()?.uppercase() ?: "A",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Admin",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                
                // Tabs para filtrar por estado
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            tabTitles.forEachIndexed { index, title ->
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            when (index) {
                                                0 -> if (selectedTabIndex == 0) AccentOrange.copy(alpha = 0.1f) else Color.Transparent
                                                1 -> if (selectedTabIndex == 1) SuccessGreen.copy(alpha = 0.1f) else Color.Transparent
                                                2 -> if (selectedTabIndex == 2) ErrorRed.copy(alpha = 0.1f) else Color.Transparent
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable { selectedTabIndex = index }
                                ) {
                                    Text(
                                        text = title,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        color = when (index) {
                                            0 -> if (selectedTabIndex == 0) AccentOrange else MaterialTheme.colorScheme.onBackground
                                            1 -> if (selectedTabIndex == 1) SuccessGreen else MaterialTheme.colorScheme.onBackground
                                            2 -> if (selectedTabIndex == 2) ErrorRed else MaterialTheme.colorScheme.onBackground
                                            else -> MaterialTheme.colorScheme.onBackground
                                        },
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )
                                    // Indicador de pestaña seleccionada
                                    if (selectedTabIndex == index) {
                                        HorizontalDivider(
                                            modifier = Modifier.fillMaxWidth(),
                                            thickness = 2.dp,
                                            color = when (index) {
                                                0 -> AccentOrange
                                                1 -> SuccessGreen
                                                2 -> ErrorRed
                                                else -> MaterialTheme.colorScheme.primary
                                            }
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.height(2.dp))
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Contenido de los tickets filtrados
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val filteredTickets = when (selectedTabIndex) {
                        0 -> tickets.filter { 
                            it.safeStatus == TicketStatus.OPEN || it.safeStatus == TicketStatus.IN_PROGRESS 
                        } // Pendientes
                        1 -> tickets.filter { it.safeStatus == TicketStatus.RESOLVED } // Completados
                        2 -> tickets.filter { it.safeStatus == TicketStatus.CLOSED } // Cancelados
                        else -> tickets
                    }
                    
                    if (filteredTickets.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay tickets en esta categoría",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredTickets) { ticket ->
                                AdminTicketCard(
                                    ticket = ticket,
                                    onClick = { onTicketClick(ticket) }
                                )
                            }
                        }
                    }
                }
            }
            
            // Menú hamburguesa
            if (showMenu) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .wrapContentHeight()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Opción Usuarios
                        TextButton(
                            onClick = {
                                showMenu = false
                                onNavigateToUserManagement()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("👥 Usuarios")
                            }
                        }
                        
                        // Opción Estadísticas
                        TextButton(
                            onClick = {
                                showMenu = false
                                onNavigateToStatistics()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("📊 Estadísticas")
                            }
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        // Opción Perfil
                        TextButton(
                            onClick = {
                                showMenu = false
                                onNavigateToProfile()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Perfil")
                            }
                        }
                        
                        // Opción Cerrar sesión
                        TextButton(
                            onClick = {
                                showMenu = false
                                onLogout()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "Cerrar sesión",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminTicketCard(
    ticket: Ticket,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Título y prioridad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = ticket.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                AdminPriorityChip(priority = ticket.priority)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Fecha
            Text(
                text = ticket.createdAt.split("T")[0],
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Usuario asignado
            ticket.assignee?.let { assignee ->
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Asignado a: ${assignee.firstName} ${assignee.lastName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun AdminPriorityChip(priority: TicketPriority) {
    val (color, text) = when (priority) {
        TicketPriority.LOW -> SuccessGreen to "LOW"
        TicketPriority.MEDIUM -> AccentOrange to "MEDIUM"
        TicketPriority.HIGH -> ErrorRed to "HIGH"
        TicketPriority.URGENT -> ErrorRed to "URGENT"
    }
    
    Surface(
        color = color,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.clip(RoundedCornerShape(4.dp))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}

package com.uade.ticket_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
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
    
    val tabTitles = listOf("Asignar", "Pendientes", "Completados", "Cancelados")
    
    LaunchedEffect(Unit) {
        viewModel.loadTickets()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home Admin- Sin asignar") },
                navigationIcon = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Implementar filtros */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
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
                // Vista Observador y Usuario actual
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Vista",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Vista Observador",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
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
                
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                
                // Tabs para filtrar por estado
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            modifier = Modifier
                                .background(
                                    when (index) {
                                        0 -> if (selectedTabIndex == 0) AccentOrange.copy(alpha = 0.1f) else Color.Transparent
                                        1 -> if (selectedTabIndex == 1) AccentOrange.copy(alpha = 0.1f) else Color.Transparent
                                        2 -> if (selectedTabIndex == 2) SuccessGreen.copy(alpha = 0.1f) else Color.Transparent
                                        3 -> if (selectedTabIndex == 3) ErrorRed.copy(alpha = 0.1f) else Color.Transparent
                                        else -> Color.Transparent
                                    }
                                )
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier.padding(16.dp),
                                color = when (index) {
                                    0 -> if (selectedTabIndex == 0) AccentOrange else MaterialTheme.colorScheme.onBackground
                                    1 -> if (selectedTabIndex == 1) AccentOrange else MaterialTheme.colorScheme.onBackground
                                    2 -> if (selectedTabIndex == 2) SuccessGreen else MaterialTheme.colorScheme.onBackground
                                    3 -> if (selectedTabIndex == 3) ErrorRed else MaterialTheme.colorScheme.onBackground
                                    else -> MaterialTheme.colorScheme.onBackground
                                },
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
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
                        0 -> tickets.filter { it.assignee == null } // Sin asignar
                        1 -> tickets.filter { 
                            it.status == TicketStatus.OPEN || it.status == TicketStatus.IN_PROGRESS 
                        } // Pendientes
                        2 -> tickets.filter { it.status == TicketStatus.RESOLVED } // Completados
                        3 -> tickets.filter { it.status == TicketStatus.CLOSED } // Cancelados
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
                        // Opción Vista Observador
                        TextButton(
                            onClick = { showMenu = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Vista",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Vista Observador")
                            }
                        }
                        
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
            
            // Botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Ver",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    "✏️",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
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

package com.uade.ticket_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Menu
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
import com.uade.ticket_mobile.ui.theme.PendingColor
import com.uade.ticket_mobile.ui.theme.CompletedColor
import com.uade.ticket_mobile.ui.theme.CanceledColor
import com.uade.ticket_mobile.ui.viewmodel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketListScreen(
    viewModel: TicketViewModel = viewModel(),
    onCreateTicket: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val tickets by viewModel.tickets.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }
    
    val tabTitles = listOf("Pendientes", "Completados", "Cancelados")
    val tabStatuses = listOf(
        listOf(TicketStatus.OPEN, TicketStatus.IN_PROGRESS),
        listOf(TicketStatus.RESOLVED),
        listOf(TicketStatus.CLOSED)
    )
    
    LaunchedEffect(Unit) {
        viewModel.loadTickets()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tickets") },
                navigationIcon = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Implementar filtros */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTicket,
                modifier = Modifier.padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add, 
                    contentDescription = "Nuevo Ticket",
                    tint = Color.White
                )
            }
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
                                        0 -> if (selectedTabIndex == 0) PendingColor.copy(alpha = 0.1f) else Color.Transparent
                                        1 -> if (selectedTabIndex == 1) CompletedColor.copy(alpha = 0.1f) else Color.Transparent
                                        2 -> if (selectedTabIndex == 2) CanceledColor.copy(alpha = 0.1f) else Color.Transparent
                                        else -> Color.Transparent
                                    }
                                )
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier.padding(16.dp),
                                color = when (index) {
                                    0 -> if (selectedTabIndex == 0) PendingColor else MaterialTheme.colorScheme.onBackground
                                    1 -> if (selectedTabIndex == 1) CompletedColor else MaterialTheme.colorScheme.onBackground
                                    2 -> if (selectedTabIndex == 2) CanceledColor else MaterialTheme.colorScheme.onBackground
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
                    val filteredTickets = tickets.filter { ticket ->
                        ticket.status in tabStatuses[selectedTabIndex]
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
                                TicketCard(ticket = ticket)
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
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Perfil",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Perfil",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
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
fun TicketCard(ticket: Ticket) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Título y status en la parte superior
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
                
                StatusChip(status = ticket.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Fecha
            Text(
                text = ticket.createdAt.split("T")[0], // Solo mostrar la fecha, no la hora
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Descripción (limitada)
            Text(
                text = if (ticket.description.length > 100) {
                    "${ticket.description.take(100)}..."
                } else {
                    ticket.description
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
fun StatusChip(status: TicketStatus) {
    val (color, text) = when (status) {
        TicketStatus.OPEN -> PendingColor to "ASIGNADO"
        TicketStatus.IN_PROGRESS -> PendingColor to "SIN ASIGNAR"
        TicketStatus.RESOLVED -> CompletedColor to "COMPLETO"
        TicketStatus.CLOSED -> CanceledColor to "CANCELADO"
    }
    
    Surface(
        color = color,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.clip(RoundedCornerShape(12.dp))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PriorityChip(priority: TicketPriority) {
    val (color, text) = when (priority) {
        TicketPriority.LOW -> MaterialTheme.colorScheme.secondary to "Baja"
        TicketPriority.MEDIUM -> MaterialTheme.colorScheme.tertiary to "Media"
        TicketPriority.HIGH -> MaterialTheme.colorScheme.error to "Alta"
        TicketPriority.URGENT -> MaterialTheme.colorScheme.error to "Urgente"
    }
    
    AssistChip(
        onClick = { },
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.1f),
            labelColor = color
        )
    )
}

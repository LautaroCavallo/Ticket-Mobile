package com.uade.ticket_mobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uade.ticket_mobile.data.models.Ticket
import com.uade.ticket_mobile.data.models.TicketPriority
import com.uade.ticket_mobile.data.models.TicketStatus
import com.uade.ticket_mobile.data.models.TicketUpdateRequest
import com.uade.ticket_mobile.data.models.User
import com.uade.ticket_mobile.ui.viewmodel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTicketScreen(
    ticket: Ticket,
    onNavigateBack: () -> Unit,
    onSaveChanges: (Ticket) -> Unit,
    viewModel: TicketViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val users by viewModel.users.collectAsState()
    
    // Cargar usuarios de soporte al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }
    
    // Los usuarios ya vienen filtrados por support desde el backend
    val supportUsers = users
    
    var selectedPriority by remember { mutableStateOf(ticket.priority) }
    var selectedStatus by remember { mutableStateOf(ticket.safeStatus) }
    var description by remember { mutableStateOf(ticket.description ?: "") }
    var selectedAssignee by remember { mutableStateOf<User?>(ticket.assignee) }
    
    var showAssigneeDropdown by remember { mutableStateOf(false) }
    var showPriorityDropdown by remember { mutableStateOf(false) }
    var showStatusDropdown by remember { mutableStateOf(false) }
    
    val priorityOptions = TicketPriority.values().toList()
    val statusOptions = listOf(
        TicketStatus.OPEN,
        TicketStatus.IN_PROGRESS,
        TicketStatus.RESOLVED,
        TicketStatus.CLOSED,
        TicketStatus.CANCELED
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Ticket") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Asignar a
            Column {
                Text(
                    text = "Asignar a",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ExposedDropdownMenuBox(
                    expanded = showAssigneeDropdown,
                    onExpandedChange = { showAssigneeDropdown = !showAssigneeDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedAssignee?.let { "${it.firstName ?: ""} ${it.lastName ?: ""}".trim() } ?: "Sin asignar",
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown"
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    
                    ExposedDropdownMenu(
                        expanded = showAssigneeDropdown,
                        onDismissRequest = { showAssigneeDropdown = false }
                    ) {
                        supportUsers.forEach { user ->
                            DropdownMenuItem(
                                text = { Text("${user.firstName ?: ""} ${user.lastName ?: ""}".trim()) },
                                onClick = {
                                    selectedAssignee = user
                                    showAssigneeDropdown = false
                                }
                            )
                        }
                    }
                }
            }
            
            // Prioridad
            Column {
                Text(
                    text = "Prioridad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ExposedDropdownMenuBox(
                    expanded = showPriorityDropdown,
                    onExpandedChange = { showPriorityDropdown = !showPriorityDropdown }
                ) {
                    OutlinedTextField(
                        value = when (selectedPriority) {
                            TicketPriority.LOW -> "Low"
                            TicketPriority.MEDIUM -> "Medium"
                            TicketPriority.HIGH -> "High"
                            TicketPriority.URGENT -> "Urgent"
                        },
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown"
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    
                    ExposedDropdownMenu(
                        expanded = showPriorityDropdown,
                        onDismissRequest = { showPriorityDropdown = false }
                    ) {
                        priorityOptions.forEach { priority ->
                            DropdownMenuItem(
                                text = { 
                                    Text(when (priority) {
                                        TicketPriority.LOW -> "Low"
                                        TicketPriority.MEDIUM -> "Medium"
                                        TicketPriority.HIGH -> "High"
                                        TicketPriority.URGENT -> "Urgent"
                                    })
                                },
                                onClick = {
                                    selectedPriority = priority
                                    showPriorityDropdown = false
                                }
                            )
                        }
                    }
                }
            }
            
            // Estado
            Column {
                Text(
                    text = "Estado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ExposedDropdownMenuBox(
                    expanded = showStatusDropdown,
                    onExpandedChange = { showStatusDropdown = !showStatusDropdown }
                ) {
                    OutlinedTextField(
                        value = when (selectedStatus) {
                            TicketStatus.OPEN -> "Abierto"
                            TicketStatus.IN_PROGRESS -> "En proceso"
                            TicketStatus.RESOLVED -> "Resuelto"
                            TicketStatus.CLOSED -> "Cerrado"
                            TicketStatus.CANCELED -> "Cancelado"
                        },
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown"
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    
                    ExposedDropdownMenu(
                        expanded = showStatusDropdown,
                        onDismissRequest = { showStatusDropdown = false }
                    ) {
                        statusOptions.forEach { status ->
                            DropdownMenuItem(
                                text = { 
                                    Text(when (status) {
                                        TicketStatus.OPEN -> "Abierto"
                                        TicketStatus.IN_PROGRESS -> "En proceso"
                                        TicketStatus.RESOLVED -> "Resuelto"
                                        TicketStatus.CLOSED -> "Cerrado"
                                        TicketStatus.CANCELED -> "Cancelado"
                                    })
                                },
                                onClick = {
                                    selectedStatus = status
                                    showStatusDropdown = false
                                }
                            )
                        }
                    }
                }
            }
            
            // Descripción
            Column {
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Descripción del ticket") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        text = "CANCELAR",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Button(
                    onClick = {
                        val updateRequest = TicketUpdateRequest(
                            title = ticket.title,
                            description = description,
                            priority = selectedPriority.name.lowercase(),
                            status = selectedStatus.name.lowercase(),
                            assigneeId = selectedAssignee?.id
                        )
                        
                        viewModel.updateTicket(
                            ticketId = ticket.id,
                            request = updateRequest,
                            onSuccess = {
                                Toast.makeText(context, "Ticket actualizado exitosamente", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Guardar Cambios",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

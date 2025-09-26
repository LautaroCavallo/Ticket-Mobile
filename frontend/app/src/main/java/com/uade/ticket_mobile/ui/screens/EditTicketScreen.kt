package com.uade.ticket_mobile.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uade.ticket_mobile.data.models.Ticket
import com.uade.ticket_mobile.data.models.TicketPriority
import com.uade.ticket_mobile.data.models.TicketStatus
import com.uade.ticket_mobile.data.mock.MockData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTicketScreen(
    ticket: Ticket,
    onNavigateBack: () -> Unit,
    onSaveChanges: (Ticket) -> Unit
) {
    var selectedAssignee by remember { mutableStateOf(ticket.assignee?.let { "${it.firstName} ${it.lastName}".trim() } ?: "Juan Pérez") }
    var selectedPriority by remember { mutableStateOf(ticket.priority) }
    var selectedStatus by remember { mutableStateOf(ticket.status) }
    var description by remember { mutableStateOf(ticket.description) }
    
    var showAssigneeDropdown by remember { mutableStateOf(false) }
    var showPriorityDropdown by remember { mutableStateOf(false) }
    var showStatusDropdown by remember { mutableStateOf(false) }
    
    val assigneeOptions = MockData.allUsers.map { "${it.firstName} ${it.lastName}".trim() }
    val priorityOptions = TicketPriority.values().toList()
    val statusOptions = listOf(
        TicketStatus.OPEN,
        TicketStatus.IN_PROGRESS,
        TicketStatus.RESOLVED,
        TicketStatus.CLOSED
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
                        value = selectedAssignee,
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                modifier = Modifier.then(
                                    if (showAssigneeDropdown) Modifier else Modifier
                                )
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
                        assigneeOptions.forEach { assignee ->
                            DropdownMenuItem(
                                text = { Text(assignee) },
                                onClick = {
                                    selectedAssignee = assignee
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
                    placeholder = { Text("Cambiar mouse en rrhh") },
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
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "CANCELAR",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Button(
                    onClick = {
                        val updatedTicket = ticket.copy(
                            priority = selectedPriority,
                            status = selectedStatus,
                            description = description
                        )
                        onSaveChanges(updatedTicket)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
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

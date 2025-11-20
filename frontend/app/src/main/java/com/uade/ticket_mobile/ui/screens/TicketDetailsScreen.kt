package com.uade.ticket_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uade.ticket_mobile.data.models.Ticket
import com.uade.ticket_mobile.data.models.TicketPriority
import com.uade.ticket_mobile.data.models.TicketStatus
import com.uade.ticket_mobile.data.mock.MockData
import com.uade.ticket_mobile.ui.theme.AccentOrange
import com.uade.ticket_mobile.ui.theme.ErrorRed
import com.uade.ticket_mobile.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailsScreen(
    ticket: Ticket,
    onNavigateBack: () -> Unit,
    onEditTicket: (Ticket) -> Unit
) {
    var newComment by remember { mutableStateOf("") }
    var showPublicComments by remember { mutableStateOf(true) }
    var showPrivateComments by remember { mutableStateOf(false) }
    
    val publicComments = MockData.mockComments.filter { 
        it.ticketId == ticket.id && !it.isPrivate 
    }
    val privateComments = MockData.mockComments.filter { 
        it.ticketId == ticket.id && it.isPrivate 
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de ticket") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Información del ticket
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Ticket de prueba",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Este ticket fue creado automáticamente para testear",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Estado
                        val statusColor = when (ticket.status) {
                            TicketStatus.OPEN -> AccentOrange
                            TicketStatus.IN_PROGRESS -> AccentOrange
                            TicketStatus.RESOLVED -> SuccessGreen
                            TicketStatus.CLOSED -> ErrorRed
                            TicketStatus.CANCELED -> ErrorRed
                        }
                        
                        Surface(
                            color = statusColor,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "CANCELADO",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        TextButton(
                            onClick = { onEditTicket(ticket) }
                        ) {
                            Text("Editar")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Información adicional
                    Text(
                        text = "Creado por: ${ticket.creator.firstName} ${ticket.creator.lastName}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "Fecha: ${ticket.createdAt.split("T")[0]}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Sección de Comentarios
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Comentarios 💬",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Tabs para comentarios públicos y privados
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilterChip(
                            onClick = { 
                                showPublicComments = true
                                showPrivateComments = false
                            },
                            label = { Text("Comentarios públicos") },
                            selected = showPublicComments,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        FilterChip(
                            onClick = { 
                                showPublicComments = false
                                showPrivateComments = true
                            },
                            label = { Text("Comentarios privados") },
                            selected = showPrivateComments,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Lista de comentarios
                    if (showPublicComments) {
                        Text(
                            text = "Visibles para usuarios y administradores del ticket",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        publicComments.forEach { comment ->
                            CommentItem(comment = comment)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    
                    if (showPrivateComments) {
                        Text(
                            text = "Visibles para agentes y administradores del ticket",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        privateComments.forEach { comment ->
                            CommentItem(comment = comment, isPrivate = true)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Campo para nuevo comentario
                    OutlinedTextField(
                        value = newComment,
                        onValueChange = { newComment = it },
                        label = { Text("Agregar comentario...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        trailingIcon = {
                            IconButton(
                                onClick = { 
                                    // TODO: Agregar comentario
                                    newComment = ""
                                }
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Enviar")
                            }
                        }
                    )
                }
            }
            
            // Sección de Archivos adjuntos
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Archivos adjuntos:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Lista de archivos (mock)
                    repeat(2) { index ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Archivo",
                                modifier = Modifier.size(16.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = "documento${index + 1}.pdf",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            
                            TextButton(onClick = { /* TODO: Descargar */ }) {
                                Text("Descargar")
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Botón para agregar archivo
                    OutlinedButton(
                        onClick = { /* TODO: Agregar archivo */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Agregar",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar archivo")
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: MockData.TicketComment,
    isPrivate: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (isPrivate) MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.primary
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.author.firstName?.firstOrNull()?.toString()?.uppercase() 
                    ?: comment.author.username?.firstOrNull()?.toString()?.uppercase()
                    ?: comment.author.email.first().toString().uppercase(),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Contenido del comentario
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${comment.author.firstName} ${comment.author.lastName}".trim(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (isPrivate) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "PRIVADO",
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp
                        )
                    }
                }
            }
            
            Text(
                text = comment.createdAt.split("T")[0],
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

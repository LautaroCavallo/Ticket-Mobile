package com.uade.ticket_mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uade.ticket_mobile.data.models.TicketCategory
import com.uade.ticket_mobile.data.models.TicketPriority
import com.uade.ticket_mobile.ui.viewmodel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketScreen(
    viewModel: TicketViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onTicketCreated: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(TicketPriority.MEDIUM) }
    var selectedCategory by remember { mutableStateOf<TicketCategory?>(null) }
    var categories by remember { mutableStateOf<List<TicketCategory>>(emptyList()) }
    
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        uiState.accessToken?.let { token ->
            // Cargar categorías cuando se abra la pantalla
            // TODO: Implementar carga de categorías
        }
    }
    
    LaunchedEffect(uiState.error) {
        if (uiState.error == null && title.isNotBlank() && description.isNotBlank()) {
            onTicketCreated()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Ticket") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título del ticket
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título del ticket") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = title.isBlank() && uiState.error != null
            )
            
            // Descripción del ticket
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                isError = description.isBlank() && uiState.error != null
            )
            
            // Prioridad
            Text(
                text = "Prioridad",
                style = MaterialTheme.typography.titleMedium
            )
            
            TicketPriority.values().forEach { priority ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedPriority == priority,
                            onClick = { selectedPriority = priority },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedPriority == priority,
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (priority) {
                            TicketPriority.LOW -> "Baja"
                            TicketPriority.MEDIUM -> "Media"
                            TicketPriority.HIGH -> "Alta"
                            TicketPriority.URGENT -> "Urgente"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón de crear
            Button(
                onClick = {
                    viewModel.createTicket(
                        title = title,
                        description = description,
                        priority = selectedPriority.name,
                        categoryId = selectedCategory?.id
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !uiState.isLoading && title.isNotBlank() && description.isNotBlank()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Crear Ticket")
                }
            }
            
            // Mostrar errores
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

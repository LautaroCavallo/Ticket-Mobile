package com.uade.ticket_mobile.ui.screens

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.uade.ticket_mobile.data.models.TicketCategory
import com.uade.ticket_mobile.data.models.TicketPriority
import com.uade.ticket_mobile.ui.viewmodel.TicketViewModel
import com.uade.ticket_mobile.utils.ImageUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CreateTicketScreen(
    viewModel: TicketViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onTicketCreated: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(TicketPriority.MEDIUM) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var compressedImageFile by remember { mutableStateOf<File?>(null) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    
    val uiState by viewModel.uiState.collectAsState()
    
    // Permiso de cámara
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    // Función para crear archivo temporal
    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(null) ?: context.cacheDir
        return File.createTempFile("TICKET_${timeStamp}_", ".jpg", storageDir)
    }
    
    // Launcher para la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && capturedImageUri != null) {
            // Comprimir la imagen
            val compressed = ImageUtils.compressImage(context, capturedImageUri!!)
            if (compressed != null) {
                compressedImageFile = compressed
                Toast.makeText(
                    context,
                    "Imagen capturada: ${String.format("%.2f", ImageUtils.getFileSizeInMB(compressed))} MB",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(context, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
                capturedImageUri = null
            }
        }
    }
    
    // Función para abrir la cámara
    fun openCamera() {
        when {
            cameraPermissionState.status.isGranted -> {
                try {
                    val imageFile = createImageFile()
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        imageFile
                    )
                    capturedImageUri = uri
                    cameraLauncher.launch(uri)
                } catch (e: Exception) {
                    Toast.makeText(context, "Error al abrir la cámara: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
            cameraPermissionState.status.shouldShowRationale -> {
                showPermissionDialog = true
            }
            else -> {
                cameraPermissionState.launchPermissionRequest()
            }
        }
    }
    
    // Observar cuando se cree el ticket exitosamente
    LaunchedEffect(uiState.ticketCreatedSuccessfully) {
        if (uiState.ticketCreatedSuccessfully) {
            onTicketCreated()
            viewModel.resetTicketCreated()
            // Limpiar imagen temporal
            compressedImageFile?.delete()
        }
    }
    
    // Diálogo de permisos
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permiso de Cámara") },
            text = { Text("Esta aplicación necesita acceso a la cámara para poder tomar fotos de los tickets.") },
            confirmButton = {
                TextButton(onClick = {
                    cameraPermissionState.launchPermissionRequest()
                    showPermissionDialog = false
                }) {
                    Text("Permitir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Ticket") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = {
            if (uiState.error != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(uiState.error ?: "")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            Text(
                text = "Título",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Password reset not working") },
                modifier = Modifier.fillMaxWidth(),
                isError = title.isNotBlank() && title.length < 5,
                supportingText = {
                    Text(
                        text = "Mínimo 5 caracteres (${title.length}/5)",
                        color = if (title.length < 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
            
            // Descripción
            Text(
                text = "Descripción",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Can't reset my password :(") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                maxLines = 5,
                isError = description.isNotBlank() && description.length < 10,
                supportingText = {
                    Text(
                        text = "Mínimo 10 caracteres (${description.length}/10)",
                        color = if (description.length < 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
            
            // Subir imagen
            Text(
                text = "Imagen (opcional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            // Botón para abrir cámara
            OutlinedButton(
                onClick = { openCamera() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = "📷",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (compressedImageFile != null) "Cambiar foto" else "Tomar foto",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Mostrar imagen capturada
            compressedImageFile?.let { file ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    AsyncImage(
                        model = file,
                        contentDescription = "Foto capturada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Botón para eliminar imagen
                    IconButton(
                        onClick = {
                            compressedImageFile?.delete()
                            compressedImageFile = null
                            capturedImageUri = null
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Eliminar foto",
                                modifier = Modifier.padding(4.dp),
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
                Text(
                    text = "Tamaño: ${String.format("%.2f", ImageUtils.getFileSizeInMB(file))} MB",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Prioridad
            Text(
                text = "Prioridad",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            TicketPriority.values().forEach { priority ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (priority == selectedPriority),
                            onClick = { selectedPriority = priority },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (priority == selectedPriority),
                        onClick = { selectedPriority = priority }
                    )
                    Text(
                        text = when(priority) {
                            TicketPriority.LOW -> "Baja"
                            TicketPriority.MEDIUM -> "Media"
                            TicketPriority.HIGH -> "Alta"
                            TicketPriority.URGENT -> "Urgente"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón crear
            Button(
                onClick = {
                    viewModel.createTicket(
                        title = title,
                        description = description,
                        priority = selectedPriority.name.lowercase(),
                        imageFile = compressedImageFile
                    )
                },
                enabled = !uiState.isLoading && title.length >= 5 && description.length >= 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("CREAR", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

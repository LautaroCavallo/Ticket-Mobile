package com.uade.ticket_mobile.ui.screens

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.uade.ticket_mobile.data.models.Ticket
import com.uade.ticket_mobile.data.models.TicketPriority
import com.uade.ticket_mobile.data.models.TicketStatus
import com.uade.ticket_mobile.data.mock.MockData
import com.uade.ticket_mobile.ui.theme.AccentOrange
import com.uade.ticket_mobile.ui.theme.ErrorRed
import com.uade.ticket_mobile.ui.theme.SuccessGreen
import com.uade.ticket_mobile.ui.viewmodel.TicketViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailsScreen(
    ticket: Ticket,
    onNavigateBack: () -> Unit,
    onEditTicket: (Ticket) -> Unit,
    viewModel: TicketViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isDownloading by remember { mutableStateOf(false) }
    var newComment by remember { mutableStateOf("") }
    var showPublicComments by remember { mutableStateOf(true) }
    var showPrivateComments by remember { mutableStateOf(false) }
    
    val publicComments = MockData.mockComments.filter { 
        it.ticketId == ticket.id && !it.isPrivate 
    }
    val privateComments = MockData.mockComments.filter { 
        it.ticketId == ticket.id && it.isPrivate 
    }
    
    // State para attachments
    val attachments by viewModel.attachments.collectAsState()
    val uploadingAttachment by viewModel.uploadingAttachment.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    
    // Cargar attachments al inicio
    LaunchedEffect(ticket.id) {
        viewModel.loadAttachments(ticket.id)
    }
    
    // Launcher para seleccionar archivo
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                // Obtener nombre original del archivo
                val originalFileName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    cursor.getString(nameIndex)
                } ?: "archivo_${System.currentTimeMillis()}"
                
                // Copiar archivo a cache con nombre original
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.cacheDir, originalFileName)
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                
                viewModel.uploadAttachment(
                    ticketId = ticket.id,
                    file = file,
                    isPrivate = false,
                    onSuccess = {
                        Toast.makeText(context, "Archivo subido exitosamente", Toast.LENGTH_SHORT).show()
                        file.delete()
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        file.delete()
                    }
                )
            } catch (e: Exception) {
                Toast.makeText(context, "Error al procesar archivo: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }
    
    // Función para descargar imagen
    fun downloadImage(imageUrl: String) {
        coroutineScope.launch {
            isDownloading = true
            try {
                // Construir URL completa si es relativa
                val fullImageUrl = if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                    imageUrl
                } else {
                    // URL relativa, agregar el BASE_URL
                    "http://10.0.2.2:8000$imageUrl"
                }
                
                withContext(Dispatchers.IO) {
                    val url = URL(fullImageUrl)
                    val connection = url.openConnection()
                    connection.connect()
                    
                    val inputStream = connection.getInputStream()
                    val fileName = "ticket_${ticket.id}_${System.currentTimeMillis()}.jpg"
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Android 10+ - usar MediaStore
                        val values = ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/TicketMobile")
                        }
                        
                        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                        uri?.let {
                            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                    } else {
                        // Android 9 y anteriores
                        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TicketMobile")
                        if (!directory.exists()) {
                            directory.mkdirs()
                        }
                        
                        val file = File(directory, fileName)
                        FileOutputStream(file).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                        
                        // Notificar al sistema de medios
                        context.sendBroadcast(
                            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
                                data = Uri.fromFile(file)
                            }
                        )
                    }
                    
                    inputStream.close()
                }
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Imagen descargada en Imágenes/TicketMobile", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al descargar: ${e.message}", Toast.LENGTH_LONG).show()
                }
                e.printStackTrace()
            } finally {
                isDownloading = false
            }
        }
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
                    // Título del ticket
                    Text(
                        text = ticket.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Descripción del ticket
                    Text(
                        text = ticket.description ?: "Sin descripción",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Estado del ticket
                        val statusColor = when (ticket.status) {
                            TicketStatus.OPEN -> AccentOrange
                            TicketStatus.IN_PROGRESS -> AccentOrange
                            TicketStatus.RESOLVED -> SuccessGreen
                            TicketStatus.CLOSED -> ErrorRed
                            TicketStatus.CANCELED -> ErrorRed
                        }
                        
                        val statusText = when (ticket.status) {
                            TicketStatus.OPEN -> "ABIERTO"
                            TicketStatus.IN_PROGRESS -> "EN PROGRESO"
                            TicketStatus.RESOLVED -> "RESUELTO"
                            TicketStatus.CLOSED -> "CERRADO"
                            TicketStatus.CANCELED -> "CANCELADO"
                        }
                        
                        Surface(
                            color = statusColor,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = statusText,
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
                    
                    // Información del ticket
                    Text(
                        text = "Creado por: ${ticket.creator.firstName ?: ""} ${ticket.creator.lastName ?: ""}".trim(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Usuario asignado
                    ticket.assignee?.let { assignee ->
                        Text(
                            text = "Asignado a: ${assignee.firstName ?: ""} ${assignee.lastName ?: ""}".trim(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } ?: Text(
                        text = "Sin asignar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    Text(
                        text = "Fecha: ${ticket.createdAt.split("T")[0]}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    // Prioridad del ticket
                    val priorityText = when (ticket.priority) {
                        TicketPriority.LOW -> "Baja"
                        TicketPriority.MEDIUM -> "Media"
                        TicketPriority.HIGH -> "Alta"
                        TicketPriority.URGENT -> "Urgente"
                    }
                    
                    val priorityColor = when (ticket.priority) {
                        TicketPriority.LOW -> MaterialTheme.colorScheme.primary
                        TicketPriority.MEDIUM -> AccentOrange
                        TicketPriority.HIGH -> ErrorRed
                        TicketPriority.URGENT -> ErrorRed
                    }
                    
                    Text(
                        text = "Prioridad: $priorityText",
                        style = MaterialTheme.typography.bodyMedium,
                        color = priorityColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            // Sección de Imagen Adjunta
            ticket.imageUrl?.let { imageUrl ->
                // Construir URL completa si es relativa
                val fullImageUrl = if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                    imageUrl
                } else {
                    "http://10.0.2.2:8000$imageUrl"
                }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
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
                                text = "Archivo adjunto 📎",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            OutlinedButton(
                                onClick = { 
                                    if (!isDownloading) {
                                        downloadImage(fullImageUrl)
                                    }
                                },
                                enabled = !isDownloading,
                                modifier = Modifier.height(36.dp)
                            ) {
                                if (isDownloading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Descargando...")
                                } else {
                                    Text("⬇️ Descargar")
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Imagen con click para ver en tamaño completo
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    // Abrir imagen en navegador o visor externo
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(Uri.parse(fullImageUrl), "image/*")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "No se puede abrir la imagen", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(fullImageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Imagen del ticket",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                        
                        Text(
                            text = "Toca la imagen para verla en tamaño completo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
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
                    
                    // Lista de archivos real
                    if (attachments.isEmpty()) {
                        Text(
                            text = "No hay archivos adjuntos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        attachments.forEach { attachment ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📎",
                                    fontSize = 16.sp
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = attachment.originalFilename,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "${attachment.fileSize / 1024} KB",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                TextButton(onClick = { 
                                    // Descargar archivo
                                    coroutineScope.launch {
                                        try {
                                            val fullUrl = if (attachment.fileUrl.startsWith("http")) {
                                                attachment.fileUrl
                                            } else {
                                                "http://10.0.2.2:8000${attachment.fileUrl}"
                                            }
                                            
                                            withContext(Dispatchers.IO) {
                                                val url = URL(fullUrl)
                                                val connection = url.openConnection()
                                                connection.connect()
                                                
                                                val inputStream = connection.getInputStream()
                                                val fileName = attachment.originalFilename
                                                
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                                    val values = ContentValues().apply {
                                                        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                                                        put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/TicketMobile")
                                                    }
                                                    
                                                    val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                                                    uri?.let {
                                                        context.contentResolver.openOutputStream(it)?.use { outputStream ->
                                                            inputStream.copyTo(outputStream)
                                                        }
                                                    }
                                                } else {
                                                    val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "TicketMobile")
                                                    if (!directory.exists()) {
                                                        directory.mkdirs()
                                                    }
                                                    
                                                    val file = File(directory, fileName)
                                                    FileOutputStream(file).use { outputStream ->
                                                        inputStream.copyTo(outputStream)
                                                    }
                                                }
                                                
                                                inputStream.close()
                                            }
                                            
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, "Archivo descargado", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, "Error al descargar: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                            e.printStackTrace()
                                        }
                                    }
                                }) {
                                    Text("Descargar")
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Botón para agregar archivo (solo soporte y admin)
                    val canUploadFiles = currentUser?.let { user ->
                        user.role == "support" || user.role == "sysAdmin" || user.isStaff
                    } ?: false
                    
                    if (canUploadFiles) {
                        OutlinedButton(
                            onClick = { 
                                filePickerLauncher.launch("*/*")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uploadingAttachment
                        ) {
                            if (uploadingAttachment) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Subiendo...")
                            } else {
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

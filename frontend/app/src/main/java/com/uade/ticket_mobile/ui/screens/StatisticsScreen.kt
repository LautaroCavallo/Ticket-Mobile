package com.uade.ticket_mobile.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uade.ticket_mobile.data.models.*
import com.uade.ticket_mobile.ui.theme.*
import com.uade.ticket_mobile.ui.viewmodel.MetricsViewModel
import com.uade.ticket_mobile.ui.viewmodel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    ticketViewModel: TicketViewModel,
    metricsViewModel: MetricsViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Resumen", "Rendimiento", "Actividad", "Salud")
    
    // Get access token
    val uiState by ticketViewModel.uiState.collectAsState()
    val accessToken = uiState.accessToken
    
    // Collect metrics states
    val ticketsOverview by metricsViewModel.ticketsOverview.collectAsState()
    val ticketsPerformance by metricsViewModel.ticketsPerformance.collectAsState()
    val usersActivity by metricsViewModel.usersActivity.collectAsState()
    val systemHealth by metricsViewModel.systemHealth.collectAsState()
    val isLoading by metricsViewModel.isLoading.collectAsState()
    val error by metricsViewModel.error.collectAsState()
    
    // Load metrics when screen is opened
    LaunchedEffect(accessToken) {
        accessToken?.let { token ->
            metricsViewModel.loadAllMetrics(token)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        accessToken?.let { token ->
                            metricsViewModel.loadAllMetrics(token)
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
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
        ) {
            // Show error if exists
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorRed.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = ErrorRed
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage,
                            color = ErrorRed,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { metricsViewModel.clearError() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }
                }
            }
            
            // Tabs
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
                                    .clickable { selectedTabIndex = index }
                            ) {
                                Text(
                                    text = title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    color = if (selectedTabIndex == index) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onBackground
                                )
                                // Indicador de pestaña seleccionada
                                if (selectedTabIndex == index) {
                                    HorizontalDivider(
                                        modifier = Modifier.fillMaxWidth(),
                                        thickness = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                            }
                        }
                    }
                }
            }
            
            // Content based on selected tab
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                when (selectedTabIndex) {
                    0 -> ResumenTab(ticketsOverview)
                    1 -> RendimientoTab(ticketsPerformance)
                    2 -> ActividadTab(usersActivity, accessToken, metricsViewModel)
                    3 -> SaludTab(systemHealth)
                }
            }
        }
    }
}

@Composable
fun ResumenTab(overview: TicketsOverviewResponse?) {
    if (overview == null) {
        EmptyStateMessage("No hay datos de resumen disponibles")
        return
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Title
        Text(
            text = "Resumen de tickets",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        // Total tickets card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = PrimaryBlue.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total de Tickets",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = overview.statusMetrics.total.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
        }
        
        // Priority section
        Text(
            text = "Tickets por prioridad",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Donut chart
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                DonutChart(
                    data = listOf(
                        overview.priorityMetrics.low.toFloat(),
                        overview.priorityMetrics.medium.toFloat(),
                        overview.priorityMetrics.high.toFloat(),
                        overview.priorityMetrics.urgent.toFloat()
                    ),
                    colors = listOf(
                        SuccessGreen,
                        AccentOrange,
                        ErrorRed,
                        ErrorRed.copy(alpha = 0.8f)
                    )
                )
            }
            
            // Legend
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LegendItem("Baja", SuccessGreen, overview.priorityMetrics.low)
                LegendItem("Media", AccentOrange, overview.priorityMetrics.medium)
                LegendItem("Alta", ErrorRed, overview.priorityMetrics.high)
                LegendItem("Urgente", ErrorRed.copy(alpha = 0.8f), overview.priorityMetrics.urgent)
            }
        }
        
        // Status section
        Text(
            text = "Tickets por estado",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        
        // Bar chart
        BarChart(
            data = listOf(
                "Abiertos" to overview.statusMetrics.open,
                "En Progreso" to overview.statusMetrics.inProgress,
                "Resueltos" to overview.statusMetrics.resolved,
                "Cerrados" to overview.statusMetrics.closed
            ),
            colors = listOf(AccentOrange, Color(0xFF2196F3), SuccessGreen, Color.Gray),
            maxValue = maxOf(
                overview.statusMetrics.open,
                overview.statusMetrics.inProgress,
                overview.statusMetrics.resolved,
                overview.statusMetrics.closed
            )
        )
        
        // Unassigned tickets alert
        if (overview.statusMetrics.unassigned > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = WarningYellow.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = WarningYellow,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Tickets sin asignar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${overview.statusMetrics.unassigned} tickets esperando asignación",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RendimientoTab(performance: TicketsPerformanceResponse?) {
    if (performance == null) {
        EmptyStateMessage("No hay datos de rendimiento disponibles")
        return
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Métricas de Rendimiento",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        // KPI Cards
        MetricCard(
            title = "Tiempo Promedio de Resolución",
            value = String.format("%.1f hrs", performance.averageResolutionTime),
            icon = Icons.Default.Star,
            color = AccentOrange
        )
        
        MetricCard(
            title = "Tasa de Resolución",
            value = String.format("%.1f%%", performance.resolutionRate),
            icon = Icons.Default.CheckCircle,
            color = SuccessGreen
        )
        
        MetricCard(
            title = "Total Resueltos",
            value = performance.totalResolved.toString(),
            icon = Icons.Default.Done,
            color = PrimaryBlue
        )
        
        // Tickets created by period
        Text(
            text = "Tickets Creados",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SmallMetricCard(
                modifier = Modifier.weight(1f),
                title = "Hoy",
                value = performance.totalCreatedToday.toString(),
                icon = Icons.Default.DateRange
            )
            SmallMetricCard(
                modifier = Modifier.weight(1f),
                title = "Esta Semana",
                value = performance.totalCreatedThisWeek.toString(),
                icon = Icons.Default.DateRange
            )
            SmallMetricCard(
                modifier = Modifier.weight(1f),
                title = "Este Mes",
                value = performance.totalCreatedThisMonth.toString(),
                icon = Icons.Default.DateRange
            )
        }
    }
}

@Composable
fun ActividadTab(
    activity: UsersActivityResponse?,
    accessToken: String?,
    metricsViewModel: MetricsViewModel
) {
    LaunchedEffect(accessToken) {
        accessToken?.let { token ->
            metricsViewModel.loadUsersActivity(token)
        }
    }
    
    if (activity == null) {
        EmptyStateMessage("No hay datos de actividad disponibles")
        return
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Actividad de Usuarios (${activity.count})",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        items(activity.users) { user ->
            UserActivityCard(user)
        }
    }
}

@Composable
fun UserActivityCard(user: UserActivity) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.userName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                
                // Role chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = when(user.role) {
                        "sysAdmin" -> ErrorRed.copy(alpha = 0.2f)
                        "support" -> AccentOrange.copy(alpha = 0.2f)
                        else -> Color.Gray.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = when(user.role) {
                            "sysAdmin" -> "Admin"
                            "support" -> "Soporte"
                            else -> "Usuario"
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActivityStat("Creados", user.ticketsCreated)
                ActivityStat("Asignados", user.ticketsAssigned)
                ActivityStat("Resueltos", user.ticketsResolved)
                ActivityStat("Comentarios", user.commentsPosted)
            }
        }
    }
}

@Composable
fun ActivityStat(label: String, value: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun SaludTab(health: SystemHealthResponse?) {
    if (health == null) {
        EmptyStateMessage("No hay datos de salud del sistema disponibles")
        return
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Salud del Sistema",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        // System status card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when(health.status) {
                    "healthy" -> SuccessGreen.copy(alpha = 0.1f)
                    "warning" -> WarningYellow.copy(alpha = 0.1f)
                    else -> ErrorRed.copy(alpha = 0.1f)
                }
            )
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when(health.status) {
                        "healthy" -> Icons.Default.CheckCircle
                        "warning" -> Icons.Default.Warning
                        else -> Icons.Default.Info
                    },
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = when(health.status) {
                        "healthy" -> SuccessGreen
                        "warning" -> WarningYellow
                        else -> ErrorRed
                    }
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "Estado del Sistema",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when(health.status) {
                            "healthy" -> "SALUDABLE"
                            "warning" -> "ADVERTENCIA"
                            else -> "CRÍTICO"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        color = when(health.status) {
                            "healthy" -> SuccessGreen
                            "warning" -> WarningYellow
                            else -> ErrorRed
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        // System metrics
        Text(
            text = "Métricas del Sistema",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SystemMetricCard(
                modifier = Modifier.weight(1f),
                title = "Usuarios",
                value = "${health.activeUsers}/${health.totalUsers}",
                subtitle = "Activos"
            )
            SystemMetricCard(
                modifier = Modifier.weight(1f),
                title = "Tickets",
                value = health.totalTickets.toString(),
                subtitle = "Total"
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SystemMetricCard(
                modifier = Modifier.weight(1f),
                title = "Abiertos",
                value = health.openTickets.toString(),
                subtitle = "Tickets",
                color = AccentOrange
            )
            SystemMetricCard(
                modifier = Modifier.weight(1f),
                title = "Urgentes",
                value = health.urgentTickets.toString(),
                subtitle = "Prioridad Alta",
                color = ErrorRed
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SystemMetricCard(
                modifier = Modifier.weight(1f),
                title = "Sin Asignar",
                value = health.unassignedTickets.toString(),
                subtitle = "Pendientes",
                color = WarningYellow
            )
            SystemMetricCard(
                modifier = Modifier.weight(1f),
                title = "T. Respuesta",
                value = String.format("%.1f hrs", health.averageResponseTime),
                subtitle = "Promedio",
                color = PrimaryBlue
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = color
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

@Composable
fun SmallMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = PrimaryBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SystemMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    color: Color = PrimaryBlue
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EmptyStateMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.Gray
            )
            Text(
                text = message,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color, value: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DonutChart(
    data: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val animatedValues = data.map { value ->
        animateFloatAsState(
            targetValue = value,
            animationSpec = tween(1000),
            label = "donut_animation"
        ).value
    }
    
    val total = animatedValues.sum()
    
    Canvas(
        modifier = modifier.size(200.dp)
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = size.minDimension / 2f * 0.8f
        val innerRadius = radius * 0.6f
        
        var currentAngle = -90f
        
        animatedValues.forEachIndexed { index, value ->
            val sweepAngle = if (total > 0f) (value / total) * 360f else 0f
            
            drawArc(
                color = colors.getOrElse(index) { Color.Gray },
                startAngle = currentAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2f, radius * 2f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = radius - innerRadius)
            )
            
            currentAngle += sweepAngle
        }
    }
}

@Composable
fun BarChart(
    data: List<Pair<String, Int>>,
    colors: List<Color>,
    maxValue: Int,
    modifier: Modifier = Modifier
) {
    val animatedValues = data.map { (_, value) ->
        animateFloatAsState(
            targetValue = value.toFloat(),
            animationSpec = tween(1000),
            label = "bar_animation"
        ).value
    }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEachIndexed { index, (label, _) ->
                val value = animatedValues[index]
                val barHeight = if (maxValue > 0) ((value / maxValue) * 160).dp else 0.dp
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = value.toInt().toString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(barHeight)
                            .background(
                                color = colors.getOrElse(index) { Color.Gray },
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

package com.uade.ticket_mobile.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uade.ticket_mobile.data.mock.MockData
import com.uade.ticket_mobile.ui.theme.AccentOrange
import com.uade.ticket_mobile.ui.theme.ErrorRed
import com.uade.ticket_mobile.ui.theme.SuccessGreen
import com.uade.ticket_mobile.ui.theme.WarningYellow
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Resumen", "Rendimiento", "Actividad", "Comentarios")
    val statistics = MockData.getTicketStatistics()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home Admin- Estadísticas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Filtrar */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Filtrar")
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
            // Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
            
            // Contenido basado en la tab seleccionada
            when (selectedTabIndex) {
                0 -> ResumenTab(statistics)
                1 -> RendimientoTab()
                2 -> ActividadTab()
                3 -> ComentariosTab()
            }
        }
    }
}

@Composable
fun ResumenTab(statistics: MockData.TicketStatistics) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Título
        Text(
            text = "Resumen de tickets",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        // Sección Tickets por prioridad
        Text(
            text = "Tickets por prioridad",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Gráfico de dona
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                DonutChart(
                    data = listOf(
                        statistics.lowPriorityTickets.toFloat(),
                        statistics.mediumPriorityTickets.toFloat(),
                        statistics.highPriorityTickets.toFloat(),
                        statistics.urgentPriorityTickets.toFloat()
                    ),
                    colors = listOf(
                        SuccessGreen,
                        AccentOrange,
                        ErrorRed,
                        ErrorRed.copy(alpha = 0.8f)
                    )
                )
            }
            
            // Leyenda
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LegendItem("LOW", SuccessGreen, statistics.lowPriorityTickets)
                LegendItem("MEDIUM", AccentOrange, statistics.mediumPriorityTickets)
                LegendItem("HIGH", ErrorRed, statistics.highPriorityTickets)
                LegendItem("URGENT", ErrorRed.copy(alpha = 0.8f), statistics.urgentPriorityTickets)
            }
        }
        
        // Título Tickets por estado
        Text(
            text = "Tickets por estado",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        
        // Gráfico de barras
        BarChart(
            data = listOf(
                "Cancelado" to statistics.closedTickets,
                "Otro" to statistics.inProgressTickets,
                "Completo" to statistics.resolvedTickets
            ),
            colors = listOf(ErrorRed, AccentOrange, SuccessGreen),
            maxValue = maxOf(statistics.closedTickets, statistics.inProgressTickets, statistics.resolvedTickets)
        )
    }
}

@Composable
fun RendimientoTab() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Datos de rendimiento\n(Funcionalidad en desarrollo)",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ActividadTab() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Registro de actividad\n(Funcionalidad en desarrollo)",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ComentariosTab() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Análisis de comentarios\n(Funcionalidad en desarrollo)",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
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
        // Gráfico de barras simplificado
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
                    // Valor encima de la barra
                    Text(
                        text = value.toInt().toString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    // Barra
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
                    
                    // Etiqueta
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

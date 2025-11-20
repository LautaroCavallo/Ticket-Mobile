package com.uade.ticket_mobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.*
import com.uade.ticket_mobile.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val iconDescription: String
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Bienvenido a TicketMobile",
            description = "La forma más fácil de gestionar tickets empresariales desde tu dispositivo móvil",
            iconDescription = "🎫"
        ),
        OnboardingPage(
            title = "Gestiona tus Tickets",
            description = "Crea, visualiza y da seguimiento a todos tus tickets en tiempo real",
            iconDescription = "📋"
        ),
        OnboardingPage(
            title = "Colabora con tu Equipo",
            description = "Asigna tickets, agrega comentarios y mantén a todos informados",
            iconDescription = "👥"
        ),
        OnboardingPage(
            title = "Métricas en Tiempo Real",
            description = "Accede a estadísticas y reportes de rendimiento desde cualquier lugar",
            iconDescription = "📊"
        )
    )
    
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Skip button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onFinish
            ) {
                Text("Saltar")
            }
        }
        
        // Pager content
        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(pages[page])
        }
        
        // Indicators
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            activeColor = PrimaryBlue,
            inactiveColor = Color.LightGray
        )
        
        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            if (pagerState.currentPage > 0) {
                TextButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                ) {
                    Text("Atrás")
                }
            } else {
                Spacer(modifier = Modifier.width(80.dp))
            }
            
            // Next/Finish button
            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onFinish()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                )
            ) {
                Text(
                    text = if (pagerState.currentPage < pages.size - 1) "Siguiente" else "Comenzar",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon/Emoji
        Text(
            text = page.iconDescription,
            fontSize = 80.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Title
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Description
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}


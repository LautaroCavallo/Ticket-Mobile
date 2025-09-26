package com.uade.ticket_mobile.ui.theme

import androidx.compose.ui.graphics.Color

// Colores principales del design system
val PrimaryBlue = Color(0xFF2E86AB)
val SecondaryBlue = Color(0xFF74B3F0)
val AccentOrange = Color(0xFFF18F01)
val SuccessGreen = Color(0xFF8BC34A)
val ErrorRed = Color(0xFFF44336)
val WarningYellow = Color(0xFFFFEB3B)
val Purple = Color(0xFF9C27B0)
val LightGray = Color(0xFFF5F5F5)

// Colores para modo claro
val Primary = PrimaryBlue
val OnPrimary = Color.White
val Secondary = SuccessGreen
val OnSecondary = Color.White
val Tertiary = AccentOrange
val OnTertiary = Color.White
val Background = Color.White
val OnBackground = Color(0xFF1C1B1F)
val Surface = Color.White
val OnSurface = Color(0xFF1C1B1F)
val SurfaceVariant = LightGray
val OnSurfaceVariant = Color(0xFF49454F)

// Colores para modo oscuro
val DarkPrimary = SecondaryBlue
val DarkOnPrimary = Color.White
val DarkSecondary = SuccessGreen
val DarkOnSecondary = Color.White
val DarkTertiary = AccentOrange
val DarkOnTertiary = Color.White
val DarkBackground = Color(0xFF1C1B1F)
val DarkOnBackground = Color(0xFFE6E1E5)
val DarkSurface = Color(0xFF1C1B1F)
val DarkOnSurface = Color(0xFFE6E1E5)
val DarkSurfaceVariant = Color(0xFF49454F)
val DarkOnSurfaceVariant = Color(0xFFCAC4D0)

// Estados de tickets
val PendingColor = AccentOrange
val CompletedColor = SuccessGreen
val CanceledColor = ErrorRed
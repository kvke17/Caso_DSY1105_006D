package com.taller.inventario.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema de Color Neón Automotriz Modo Oscuro
private val DarkNeonColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF00202A),
    primaryContainer = NeonCyanContainer,
    onPrimaryContainer = NeonCyanOnContainer,
    secondary = NeonOrange,
    onSecondary = Color.White,
    secondaryContainer = NeonOrangeContainer,
    onSecondaryContainer = NeonOrangeOnContainer,
    tertiary = NeonPurple,
    onTertiary = Color.White,
    tertiaryContainer = NeonPurpleContainer,
    onTertiaryContainer = Color(0xFFE8DDFF),
    background = BackgroundDark,
    onBackground = Color(0xFFF1F5F9),
    surface = SurfaceDark,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = OutlineDark,
    error = StockAgotadoText,
    onError = Color.White
)

@Composable
fun InventarioTheme(
    darkTheme: Boolean = true, // Modo Oscuro Neón activado por defecto
    content: @Composable () -> Unit
) {
    val colorScheme = DarkNeonColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // Barra de estado en modo oscuro para armonía total
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

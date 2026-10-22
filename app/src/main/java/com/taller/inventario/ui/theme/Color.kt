package com.taller.inventario.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta Primaria - Azul Eléctrico Cyan Neón Ultra Brillante
val NeonCyan = Color(0xFF00F0FF)
val NeonCyanLight = Color(0xFF5CF8FF)
val NeonCyanContainer = Color(0xFF003847)
val NeonCyanOnContainer = Color(0xFF80F3FF)

// Paleta Secundaria / Acento - Naranja Turbo Neón Radiante
val NeonOrange = Color(0xFFFF5E00)
val NeonOrangeLight = Color(0xFFFF8533)
val NeonOrangeContainer = Color(0xFF4A1A00)
val NeonOrangeOnContainer = Color(0xFFFFD1A9)

// Paleta Terciaria - Púrpura Cyber Neón
val NeonPurple = Color(0xFFD000FF)
val NeonPurpleContainer = Color(0xFF3B004A)

// Verde Neón Eléctrico
val NeonGreen = Color(0xFF00FF88)

// Compatibilidad con símbolos del proyecto
val NavyPrimary = NeonCyan
val NavyPrimaryDark = NeonCyan
val NavyPrimaryContainerLight = NeonCyanContainer
val NavyPrimaryContainerDark = NeonCyanContainer

val IndustrialOrange = NeonOrange
val IndustrialOrangeLight = NeonOrangeLight
val IndustrialOrangeContainerLight = NeonOrangeContainer
val IndustrialOrangeContainerDark = NeonOrangeContainer

val ElectricCyanLight = NeonCyan
val ElectricCyanDark = NeonCyan
val ElectricCyanContainerLight = NeonCyanContainer
val ElectricCyanContainerDark = NeonCyanContainer

// Fondos y Superficies Modo Oscuro Telemetría Automotriz
val BackgroundDark = Color(0xFF0A0E17)      // Negro Carbón Espacial Profundo
val SurfaceDark = Color(0xFF121826)         // Tarjetas Titanio Oscuro Pulido
val SurfaceVariantDark = Color(0xFF1A2338)  // Superficie Elevada con tinte azulado
val OutlineDark = Color(0xFF24324F)         // Bordes sutiles

val BackgroundLight = Color(0xFF0A0E17)
val SurfaceLight = Color(0xFF121826)
val SurfaceVariantLight = Color(0xFF1A2338)
val OutlineLight = Color(0xFF24324F)

// Semántica de Estado de Inventario (Badges Neón Radiantes con Fondo Tonal)
val StockDisponibleText = Color(0xFF00FF88)       // Verde Neón Radiante
val StockDisponibleBg = Color(0xFF062B17)         // Fondo Tonal Oscuro Profundo
val StockDisponibleBorder = Color(0x8000FF88)     // Borde Neón Brillante

val StockBajoText = Color(0xFFFFB800)             // Ámbar Neón Alerta
val StockBajoBg = Color(0xFF2E1C00)               // Fondo Tonal Oscuro
val StockBajoBorder = Color(0x80FFB800)           // Borde Neón Brillante

val StockAgotadoText = Color(0xFFFF0055)          // Carmesí Neón
val StockAgotadoBg = Color(0xFF330012)            // Fondo Tonal Oscuro
val StockAgotadoBorder = Color(0x80FF0055)        // Borde Neón Brillante

// Colores Temáticos Neón Vivos por Categoría Automotriz
fun getCategoryThemeColor(categoria: String): Color {
    return when (categoria.lowercase()) {
        "lubricantes" -> Color(0xFF00F0FF) // Cyan Láser Neón
        "filtros" -> Color(0xFF00FFA3)     // Menta Radioactiva Neón
        "frenos" -> Color(0xFFFF0055)      // Carmesí Brembo Neón
        "encendido" -> Color(0xFFFFE500)   // Chispa Amarilla Neón
        "baterías" -> Color(0xFFD000FF)    // Púrpura Cyberpunk Neón
        "suspensión" -> Color(0xFF7000FF)  // Violeta Mecánico Neón
        "neumáticos" -> Color(0xFF00B4D8)  // Azul Cobalto Eléctrico
        else -> Color(0xFF00FF88)          // Verde Neón
    }
}

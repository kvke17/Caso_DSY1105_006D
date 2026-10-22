package com.taller.inventario.ui.producto.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taller.inventario.data.local.ProductoEntity
import com.taller.inventario.ui.theme.StockAgotadoBg
import com.taller.inventario.ui.theme.StockAgotadoBorder
import com.taller.inventario.ui.theme.StockAgotadoText
import com.taller.inventario.ui.theme.StockBajoBg
import com.taller.inventario.ui.theme.StockBajoBorder
import com.taller.inventario.ui.theme.StockBajoText
import com.taller.inventario.ui.theme.StockDisponibleBg
import com.taller.inventario.ui.theme.StockDisponibleBorder
import com.taller.inventario.ui.theme.StockDisponibleText

/**
 * Badge de estado de inventario accesible y visualmente estilizado.
 * Combina fondo tonal suave, borde contrastado, icono distintivo y tipografía clara.
 */
@Composable
fun StockBadge(
    estado: String,
    modifier: Modifier = Modifier
) {
    val (bgColor: Color, textColor: Color, borderColor: Color, icon: ImageVector, label: String) = when (estado) {
        ProductoEntity.ESTADO_AGOTADO -> BadgeConfig(
            bgColor = StockAgotadoBg,
            textColor = StockAgotadoText,
            borderColor = StockAgotadoBorder,
            icon = Icons.Rounded.ErrorOutline,
            label = "Agotado"
        )
        ProductoEntity.ESTADO_BAJO_STOCK -> BadgeConfig(
            bgColor = StockBajoBg,
            textColor = StockBajoText,
            borderColor = StockBajoBorder,
            icon = Icons.Rounded.Warning,
            label = "Bajo Stock"
        )
        else -> BadgeConfig(
            bgColor = StockDisponibleBg,
            textColor = StockDisponibleText,
            borderColor = StockDisponibleBorder,
            icon = Icons.Rounded.CheckCircle,
            label = "Disponible"
        )
    }

    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                color = textColor,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                )
            )
        }
    }
}

private data class BadgeConfig(
    val bgColor: Color,
    val textColor: Color,
    val borderColor: Color,
    val icon: ImageVector,
    val label: String
)

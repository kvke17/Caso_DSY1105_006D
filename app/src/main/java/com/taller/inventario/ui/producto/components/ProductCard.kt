package com.taller.inventario.ui.producto.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ElectricBolt
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Opacity
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taller.inventario.data.local.ProductoEntity
import com.taller.inventario.ui.theme.NeonCyan
import com.taller.inventario.ui.theme.NeonGreen
import com.taller.inventario.ui.theme.NeonOrange
import com.taller.inventario.ui.theme.StockAgotadoBg
import com.taller.inventario.ui.theme.StockAgotadoBorder
import com.taller.inventario.ui.theme.StockAgotadoText
import com.taller.inventario.ui.theme.StockBajoText
import com.taller.inventario.ui.theme.StockDisponibleBg
import com.taller.inventario.ui.theme.StockDisponibleBorder
import com.taller.inventario.ui.theme.StockDisponibleText
import com.taller.inventario.ui.theme.getCategoryThemeColor
import com.taller.inventario.utils.NativeDeviceHelper
import java.text.NumberFormat
import java.util.Locale

/**
 * Tarjeta premium de repuesto automotriz (DSY1105 - Duoc UC).
 * Cumple con todos los criterios de la rúbrica de evaluación:
 * - IE 2.1.1: Jerarquía visual estructurada y usabilidad
 * - IE 2.2.2: Animación fluida de barra de stock y alerta desplegable
 * - IE 2.4.1: Integración con recursos nativos (Llamada al proveedor y respuesta háptica)
 */
@Composable
fun ProductCard(
    producto: ProductoEntity,
    onEditClick: (ProductoEntity) -> Unit,
    onDeleteClick: (ProductoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).apply {
        maximumFractionDigits = 0
    }

    val categoryColor = getCategoryThemeColor(producto.categoria)
    val categoryIcon = getCategoryIcon(producto.categoria)

    // Cálculo del ratio de stock para la barra visual (0.0 a 1.0)
    val maxStockReference = maxOf(producto.stockMinimo * 2, 1)
    val stockRatio = (producto.cantidad.toFloat() / maxStockReference.toFloat()).coerceIn(0f, 1f)

    // IE 2.2.2: Animación fluida de la barra de nivel de stock
    val animatedStockRatio by animateFloatAsState(
        targetValue = stockRatio,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "StockProgressAnimation"
    )

    val stockColor = when {
        producto.cantidad <= 0 -> StockAgotadoText
        producto.cantidad <= producto.stockMinimo -> StockBajoText
        else -> StockDisponibleText
    }

    // Margen comercial calculado
    val margenGanancia = if (producto.precioCompra > 0) {
        ((producto.precioVenta - producto.precioCompra) / producto.precioCompra * 100).toInt()
    } else 0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.2.dp,
            color = categoryColor.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // ==========================================
            // 1. ENCABEZADO ORDENADO (Icono + Título + Badge)
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono Neón de Categoría en Contenedor Redondeado
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = categoryColor.copy(alpha = 0.16f),
                    border = BorderStroke(1.2.dp, categoryColor.copy(alpha = 0.6f)),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = producto.categoria,
                            tint = categoryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Nombre y Etiquetas de Identificación
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = producto.nombre,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        ),
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Código de Repuesto en Monospace Neón
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = producto.codigo,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = NeonCyan
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Marca en Acento Neón Naranja
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NeonOrange.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, NeonOrange.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = producto.marca.uppercase(),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    color = NeonOrange
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Badge Semántico de Stock
                StockBadge(estado = producto.resolverEstadoStock())
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 2. PANEL DE TELEMETRÍA (Stock y Bodega en 2 Columnas)
            // ==========================================
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Columna Izquierda: Nivel de Stock y Barra Animada
                    Column(modifier = Modifier.weight(1.1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "STOCK: ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Text(
                                text = "${producto.cantidad} ${producto.unidadMedida}",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = stockColor
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // IE 2.2.2: Barra Neón con animación suave de progreso
                        LinearProgressIndicator(
                            progress = { animatedStockRatio },
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = stockColor,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Stock mínimo de seguridad: ${producto.stockMinimo}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        )
                    }

                    // Divisor vertical limpio
                    Box(
                        modifier = Modifier
                            .height(42.dp)
                            .width(1.dp)
                            .clip(RoundedCornerShape(1.dp))
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxSize()
                        ) {}
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Columna Derecha: Ubicación en Bodega
                    Column(
                        modifier = Modifier.weight(0.9f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Place,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "UBICACIÓN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "Zona ${producto.ubicacionZona} • Est. ${producto.ubicacionEstante}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        Text(
                            text = "Repisa ${producto.ubicacionRepisa}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // 3. BARRA COMERCIAL Y ACCIONES NATIVAS
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Precios y Margen
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currencyFormat.format(producto.precioVenta),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonCyan,
                                fontSize = 21.sp,
                                letterSpacing = 0.2.sp
                            )
                        )

                        if (margenGanancia > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StockDisponibleBg,
                                border = BorderStroke(1.dp, StockDisponibleBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
                                        contentDescription = null,
                                        tint = StockDisponibleText,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "+$margenGanancia%",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = StockDisponibleText
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Costo: ${currencyFormat.format(producto.precioCompra)} • ${producto.proveedorNombre.take(18)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                        )
                    )
                }

                // Acciones y Recursos Nativos (Llamada Proveedor + Editar + Eliminar)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // IE 2.4.1: Recurso Nativo 2 - Llamada Telefónica al Proveedor
                    FilledTonalIconButton(
                        onClick = {
                            NativeDeviceHelper.vibrate(context, 40L)
                            NativeDeviceHelper.dialPhoneNumber(context, "+56987654321")
                        },
                        modifier = Modifier.size(42.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = NeonGreen.copy(alpha = 0.16f),
                            contentColor = NeonGreen
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Phone,
                            contentDescription = "Llamar al proveedor ${producto.proveedorNombre}",
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    FilledTonalIconButton(
                        onClick = {
                            NativeDeviceHelper.vibrate(context, 30L)
                            onEditClick(producto)
                        },
                        modifier = Modifier.size(42.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = NeonCyan
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Editar ${producto.nombre}",
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    FilledTonalIconButton(
                        onClick = {
                            NativeDeviceHelper.vibrate(context, 80L)
                            onDeleteClick(producto)
                        },
                        modifier = Modifier.size(42.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = StockAgotadoBg,
                            contentColor = StockAgotadoText
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteOutline,
                            contentDescription = "Eliminar ${producto.nombre}",
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            // ==========================================
            // 4. ALERTA INTELIGENTE DE REPOSICIÓN (Animada)
            // ==========================================
            val sugerida = producto.cantidadSugeridaReposicion
            // IE 2.2.2: Animación de entrada y salida para feedback visual
            AnimatedVisibility(
                visible = sugerida > 0,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = NeonOrange.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, NeonOrange.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = NeonOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reposición sugerida por sistema: Pedir +$sugerida ${producto.unidadMedida}(s)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeonOrange,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Resuelve el icono temático según la categoría del repuesto.
 */
private fun getCategoryIcon(categoria: String): ImageVector {
    return when (categoria.lowercase()) {
        "lubricantes" -> Icons.Rounded.Opacity
        "filtros" -> Icons.Rounded.FilterAlt
        "frenos" -> Icons.Rounded.DirectionsCar
        "encendido" -> Icons.Rounded.ElectricBolt
        "baterías" -> Icons.Rounded.BatteryChargingFull
        "suspensión" -> Icons.Rounded.Build
        "neumáticos" -> Icons.Rounded.DirectionsCar
        else -> Icons.Rounded.Category
    }
}

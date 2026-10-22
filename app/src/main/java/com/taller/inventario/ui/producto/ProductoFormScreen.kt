package com.taller.inventario.ui.producto

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.taller.inventario.data.local.ProductoEntity
import com.taller.inventario.ui.producto.components.StockBadge
import com.taller.inventario.ui.theme.IndustrialOrange
import com.taller.inventario.ui.theme.StockDisponibleBg
import com.taller.inventario.ui.theme.StockDisponibleText
import com.taller.inventario.utils.NativeDeviceHelper
import java.text.NumberFormat
import java.util.Locale

/**
 * Formulario estructurado y reactivo para alta y edición de repuestos automotrices.
 * Organizado en 3 secciones funcionales con feedback de validación reactivo en Compose,
 * escaneo QR con cámara nativa y estimador de margen en tiempo real.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    codigoParaEditar: String?,
    uiState: ProductoUiState,
    viewModel: ProductoViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToScanner: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).apply {
        maximumFractionDigits = 0
    }

    // Carga inicial según el modo (Crear o Editar)
    LaunchedEffect(codigoParaEditar) {
        if (!codigoParaEditar.isNullOrBlank()) {
            viewModel.cargarProductoParaEditar(codigoParaEditar)
        } else {
            viewModel.prepararNuevoProducto()
        }
    }

    // Efecto reactivo al guardar con éxito en Room
    LaunchedEffect(uiState.guardadoExitosamente) {
        if (uiState.guardadoExitosamente) {
            viewModel.resetGuardadoExitosamente()
            onNavigateBack()
        }
    }

    // Efecto reactivo ante errores generales
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.limpiarMensajeError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (uiState.isEditing) "Editar Repuesto" else "Nuevo Repuesto",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = if (uiState.isEditing) "Código: ${uiState.codigo}" else "Registro en Inventario Central",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ==========================================
            // SECCIÓN 1: IDENTIFICACIÓN DEL PRODUCTO
            // ==========================================
            SectionCard(
                title = "1. Identificación y Clasificación",
                icon = Icons.Rounded.Category
            ) {
                // Campo Código con Botón QR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    OutlinedTextField(
                        value = uiState.codigo,
                        onValueChange = { viewModel.onCodigoChange(it) },
                        label = { Text("Código de Repuesto *") },
                        placeholder = { Text("ej. ACE-CAST-5W30") },
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isEditing, // Clave primaria no editable
                        isError = uiState.codigoError != null,
                        supportingText = {
                            uiState.codigoError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (!uiState.isEditing) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            onClick = onNavigateToScanner,
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.QrCodeScanner,
                                    contentDescription = "Escanear código con cámara",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                // Nombre
                OutlinedTextField(
                    value = uiState.nombre,
                    onValueChange = { viewModel.onNombreChange(it) },
                    label = { Text("Nombre del Repuesto *") },
                    placeholder = { Text("ej. Aceite Sintético Edge 5W-30 4L") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.nombreError != null,
                    supportingText = {
                        uiState.nombreError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Marca
                OutlinedTextField(
                    value = uiState.marca,
                    onValueChange = { viewModel.onMarcaChange(it) },
                    label = { Text("Marca Automotriz *") },
                    placeholder = { Text("ej. Castrol, Bosch, Mann Filter, Brembo, NGK") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.marcaError != null,
                    supportingText = {
                        uiState.marcaError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Categoría (Chips seleccionables)
                Text(
                    text = "Categoría del Repuesto *",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProductoEntity.CATEGORIAS.forEach { cat ->
                        val isSelected = uiState.categoria == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onCategoriaChange(cat) },
                            label = { Text(cat) },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                if (uiState.categoriaError != null) {
                    Text(
                        text = uiState.categoriaError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // ==========================================
            // SECCIÓN 2: STOCK Y PRECIOS REFERENCIALES
            // ==========================================
            SectionCard(
                title = "2. Control de Stock y Valores",
                icon = Icons.Rounded.MonetizationOn
            ) {
                // Unidad de Medida Selector
                Text(
                    text = "Unidad de Medida / Presentación",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProductoEntity.UNIDADES_MEDIDA.forEach { unidad ->
                        val isSelected = uiState.unidadMedida == unidad
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onUnidadMedidaChange(unidad) },
                            label = { Text(unidad) },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Cantidad y Stock Mínimo en dos columnas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.cantidad,
                        onValueChange = { viewModel.onCantidadChange(it) },
                        label = { Text("Cantidad Actual *") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = uiState.cantidadError != null,
                        supportingText = {
                            uiState.cantidadError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = uiState.stockMinimo,
                        onValueChange = { viewModel.onStockMinimoChange(it) },
                        label = { Text("Stock Mínimo *") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = uiState.stockMinimoError != null,
                        supportingText = {
                            uiState.stockMinimoError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Estado de Inventario resultante en tiempo real
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Estado según stock actual:",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        StockBadge(estado = uiState.estado)
                    }
                }

                // REGLA DE NEGOCIO: Proyección de reposición sugerida
                val reposicionSugerida = uiState.cantidadSugeridaReposicionCalculada
                Surface(
                    color = if (reposicionSugerida > 0) IndustrialOrange.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (reposicionSugerida > 0) IndustrialOrange.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = if (reposicionSugerida > 0) IndustrialOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Reposición Mínima Sugerida: $reposicionSugerida ${uiState.unidadMedida.lowercase()}(s)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (reposicionSugerida > 0) IndustrialOrange else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Text(
                                text = "Regla taller: maxOf(0, (Stock Mínimo × 2) - Cantidad)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                // Precios de Compra y Venta
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.precioCompra,
                        onValueChange = { viewModel.onPrecioCompraChange(it) },
                        label = { Text("Precio Costo ($) *") },
                        placeholder = { Text("ej. 25000") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = uiState.precioVenta,
                        onValueChange = { viewModel.onPrecioVentaChange(it) },
                        label = { Text("Precio Venta ($) *") },
                        placeholder = { Text("ej. 38000") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Estimador de Margen Comercial en Vivo
                val compraNum = uiState.precioCompra.toDoubleOrNull() ?: 0.0
                val ventaNum = uiState.precioVenta.toDoubleOrNull() ?: 0.0
                if (compraNum > 0.0 && ventaNum >= compraNum) {
                    val margenMonto = ventaNum - compraNum
                    val margenPct = ((margenMonto / compraNum) * 100).toInt()

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = StockDisponibleBg,
                        border = BorderStroke(1.dp, StockDisponibleText.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
                                contentDescription = null,
                                tint = StockDisponibleText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Margen Proyectado: +$margenPct% (Utilidad: ${currencyFormat.format(margenMonto)} por unidad)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = StockDisponibleText,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                if (uiState.precioError != null) {
                    Text(
                        text = uiState.precioError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // ==========================================
            // SECCIÓN 3: UBICACIÓN Y PROVEEDOR
            // ==========================================
            SectionCard(
                title = "3. Ubicación en Bodega y Proveedor",
                icon = Icons.Rounded.Place
            ) {
                Text(
                    text = "Coordenadas físicas en taller automotriz:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.ubicacionZona,
                        onValueChange = { viewModel.onUbicacionZonaChange(it) },
                        label = { Text("Zona") },
                        placeholder = { Text("A") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = uiState.ubicacionEstante,
                        onValueChange = { viewModel.onUbicacionEstanteChange(it) },
                        label = { Text("Estante") },
                        placeholder = { Text("1") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = uiState.ubicacionRepisa,
                        onValueChange = { viewModel.onUbicacionRepisaChange(it) },
                        label = { Text("Repisa") },
                        placeholder = { Text("A") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Proveedor principal asignado
                OutlinedTextField(
                    value = uiState.proveedorNombre,
                    onValueChange = { viewModel.onProveedorNombreChange(it) },
                    label = { Text("Proveedor Principal Asignado") },
                    placeholder = { Text("ej. Repuestos Chile SpA, Lubricentro Central") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Storefront,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // BOTONES DE ACCIÓN: CANCELAR Y GUARDAR
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        "Cancelar",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        NativeDeviceHelper.vibrate(context, 50L)
                        viewModel.validarYGuardar()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    enabled = !uiState.guardando,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    if (uiState.guardando) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Save,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.isEditing) "Actualizar" else "Guardar Repuesto",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            content()
        }
    }
}

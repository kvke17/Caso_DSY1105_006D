package com.taller.inventario.ui.producto

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.PrecisionManufacturing
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taller.inventario.data.local.ProductoEntity
import com.taller.inventario.ui.producto.components.ProductCard
import com.taller.inventario.ui.theme.NeonCyan
import com.taller.inventario.ui.theme.NeonGreen
import com.taller.inventario.ui.theme.NeonOrange
import com.taller.inventario.ui.theme.NeonPurple
import com.taller.inventario.ui.theme.getCategoryThemeColor
import java.text.NumberFormat
import java.util.Locale

/**
 * Pantalla principal del catálogo con diseño ultra ordenado, limpio y colores neón de alto impacto.
 * Cuenta con panel de métricas consolidado (2 tarjetas maestras equilibradas),
 * buscador reactivo con acceso directo a QR, selector de categorías por color temático
 * y tarjetas de producto con cuadrícula interna de datos técnicos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaProductosScreen(
    uiState: ProductoUiState,
    viewModel: ProductoViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToScanner: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = NeonOrange,
                            shadowElevation = 6.dp,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.PrecisionManufacturing,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Taller San Cristóbal",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    letterSpacing = 0.4.sp,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Gestión de Inventario Automotriz • Puente Alto",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = NeonCyan
                                )
                            )
                        }
                    }
                },
                actions = {
                    // Botón Neón de acceso directo al escáner QR
                    IconButton(onClick = onNavigateToScanner) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.6f)),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.QrCodeScanner,
                                    contentDescription = "Escanear QR de Repuesto",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = NeonOrange,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Agregar nuevo repuesto",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ==========================================
            // 1. DASHBOARD ORDENADO (2 Tarjetas Maestras Equilibradas)
            // ==========================================
            ConsolidatedDashboard(
                productos = uiState.productos,
                soloBajoStock = uiState.soloBajoStock,
                onToggleBajoStock = { viewModel.toggleSoloBajoStock() }
            )

            // ==========================================
            // 2. BUSCADOR Y ACCESO QR ULTRA REDONDEADO (20dp)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "Buscar por repuesto, código, marca, proveedor...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Buscar",
                            tint = NeonCyan
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = "Limpiar texto",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Botón Neón de Escáner QR
                Surface(
                    onClick = onNavigateToScanner,
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.7f)),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.QrCodeScanner,
                            contentDescription = "Escanear código con cámara",
                            tint = NeonCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // ==========================================
            // 3. CHIPS DE CATEGORÍA CON COLORES NEÓN VIVOS
            // ==========================================
            val categoriasConTodas = listOf("Todas") + ProductoEntity.CATEGORIAS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                categoriasConTodas.forEach { categoria ->
                    val isSelected = uiState.filtroCategoria == categoria
                    val count = if (categoria == "Todas") {
                        uiState.productos.size
                    } else {
                        uiState.productos.count { it.categoria.equals(categoria, ignoreCase = true) }
                    }

                    val catColor = if (categoria == "Todas") NeonCyan else getCategoryThemeColor(categoria)

                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onFiltroCategoriaChange(categoria) },
                        label = {
                            Text(
                                text = if (count > 0 && categoria != "Todas") "$categoria ($count)" else categoria,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = catColor,
                            selectedLabelColor = Color(0xFF00202A),
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = if (isSelected) Color(0xFF00202A) else catColor
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = catColor,
                            borderWidth = 1.2.dp
                        )
                    )
                }
            }

            // ==========================================
            // 4. LISTA DE PRODUCTOS O ESTADO VACÍO
            // ==========================================
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = NeonCyan,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Sincronizando repuestos...",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NeonCyan.copy(alpha = 0.85f)
                            )
                        )
                    }
                }
            } else if (uiState.productos.isEmpty()) {
                EmptyInventoryState(
                    query = uiState.searchQuery,
                    categoria = uiState.filtroCategoria,
                    onResetFilters = {
                        viewModel.onSearchQueryChange("")
                        viewModel.onFiltroCategoriaChange("Todas")
                        if (uiState.soloBajoStock) viewModel.toggleSoloBajoStock()
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = uiState.productos,
                        key = { it.codigo }
                    ) { producto ->
                        ProductCard(
                            producto = producto,
                            onEditClick = { onNavigateToEdit(it.codigo) },
                            onDeleteClick = { viewModel.solicitarEliminacion(it) }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de Confirmación de Eliminación
    uiState.productoAEliminar?.let { prod ->
        AlertDialog(
            onDismissRequest = { viewModel.cancelarEliminacion() },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            icon = {
                Icon(
                    imageVector = Icons.Rounded.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Confirmar Eliminación",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "¿Está seguro de eliminar el repuesto '${prod.nombre}' (Código: ${prod.codigo})?\n\nEsta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.confirmarEliminacion() }
                ) {
                    Text(
                        "Eliminar Repuesto",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelarEliminacion() }) {
                    Text(
                        "Cancelar",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}

/**
 * Panel de métricas consolidado en 2 tarjetas simétricas y limpias:
 * 1. Resumen de inventario activo y valorización total en pesos.
 * 2. Monitor de stock crítico y reposiciones sugeridas con filtro interactivo.
 */
@Composable
private fun ConsolidatedDashboard(
    productos: List<ProductoEntity>,
    soloBajoStock: Boolean,
    onToggleBajoStock: () -> Unit
) {
    val totalItems = productos.size
    val bajosStock = productos.count { it.cantidad <= it.stockMinimo }
    val reposicionesTotales = productos.sumOf { it.cantidadSugeridaReposicion }
    val valorizacionTotal = productos.sumOf { it.precioCompra * it.cantidad }

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).apply {
        maximumFractionDigits = 0
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Tarjeta 1: Total Inventario & Valorización
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EN BODEGA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    )
                    Icon(
                        imageVector = Icons.Rounded.Inventory2,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$totalItems repuestos",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Val: ${currencyFormat.format(valorizacionTotal)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonPurple
                    )
                )
            }
        }

        // Tarjeta 2: Stock Crítico & Reposición (Interactiva)
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (soloBajoStock) NeonOrange.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.2.dp,
                if (soloBajoStock) NeonOrange else NeonOrange.copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            onClick = onToggleBajoStock
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (soloBajoStock) "FILTRO ACTIVO" else "STOCK CRÍTICO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonOrange
                        )
                    )
                    Icon(
                        imageVector = Icons.Rounded.WarningAmber,
                        contentDescription = null,
                        tint = NeonOrange,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$bajosStock alertas",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Pedir: +$reposicionesTotales unid.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (reposicionesTotales > 0) NeonOrange else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

/**
 * Estado vacío moderno con borde redondeado e ilustración.
 */
@Composable
private fun EmptyInventoryState(
    query: String,
    categoria: String,
    onResetFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.5.dp, NeonCyan.copy(alpha = 0.5f)),
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Inventory,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(38.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "No se encontraron repuestos",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (query.isNotBlank() || categoria != "Todas") {
                "No hay coincidencias para '$query'.\nPruebe con otro código o categoría."
            } else {
                "El inventario del taller está vacío.\nAgregue un nuevo repuesto con el botón '+' inferior."
            },
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        if (query.isNotBlank() || categoria != "Todas") {
            Spacer(modifier = Modifier.height(18.dp))
            Surface(
                onClick = onResetFilters,
                shape = RoundedCornerShape(20.dp),
                color = NeonCyan,
                border = BorderStroke(1.dp, NeonCyan)
            ) {
                Text(
                    text = "Restablecer Filtros",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00202A)
                    )
                )
            }
        }
    }
}

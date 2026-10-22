package com.taller.inventario.ui.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.taller.inventario.data.local.ProductoEntity
import com.taller.inventario.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona la lógica de negocio y el estado reactivo del inventario automotriz.
 * Expone un StateFlow inmutable conforme a la arquitectura MVVM estricta.
 */
class ProductoViewModel(
    private val repository: ProductoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductoUiState(isLoading = true))
    val uiState: StateFlow<ProductoUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _categoriaFiltro = MutableStateFlow("Todas")
    private val _soloBajoStock = MutableStateFlow(false)

    init {
        // Asegura que existan datos precargados si la BD se creó vacía
        viewModelScope.launch {
            repository.verificarYPrecargarSiVacio()
        }

        // Flujo reactivo combinado que reacciona inmediatamente a cambios en Room o filtros
        viewModelScope.launch {
            combine(
                repository.getAllProductos(),
                _searchQuery,
                _categoriaFiltro,
                _soloBajoStock
            ) { productos, query, categoria, soloBajo ->
                val filtrados = productos.filter { prod ->
                    val coincideQuery = query.isBlank() ||
                            prod.nombre.contains(query, ignoreCase = true) ||
                            prod.codigo.contains(query, ignoreCase = true) ||
                            prod.marca.contains(query, ignoreCase = true) ||
                            prod.ubicacionCompleta.contains(query, ignoreCase = true)

                    val coincideCategoria = categoria == "Todas" || prod.categoria.equals(categoria, ignoreCase = true)

                    val coincideBajoStock = !soloBajo || prod.cantidad <= prod.stockMinimo

                    coincideQuery && coincideCategoria && coincideBajoStock
                }

                _uiState.update { current ->
                    current.copy(
                        productos = filtrados,
                        searchQuery = query,
                        filtroCategoria = categoria,
                        soloBajoStock = soloBajo,
                        isLoading = false
                    )
                }
            }.collect()
        }
    }

    // --- Filtros y Búsqueda ---

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFiltroCategoriaChange(categoria: String) {
        _categoriaFiltro.value = categoria
    }

    fun toggleSoloBajoStock() {
        _soloBajoStock.value = !_soloBajoStock.value
    }

    // --- Manejo de Campos de Formulario con Limpieza Automática de Errores ---

    fun onCodigoChange(value: String) {
        _uiState.update { it.copy(codigo = value, codigoError = null) }
    }

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value, nombreError = null) }
    }

    fun onMarcaChange(value: String) {
        _uiState.update { it.copy(marca = value, marcaError = null) }
    }

    fun onCategoriaChange(value: String) {
        _uiState.update { it.copy(categoria = value, categoriaError = null) }
    }

    fun onUnidadMedidaChange(value: String) {
        _uiState.update { it.copy(unidadMedida = value) }
    }

    fun onCantidadChange(value: String) {
        // Solo permite números positivos
        val filtrado = value.filter { it.isDigit() }
        _uiState.update { current ->
            val cant = filtrado.toIntOrNull() ?: 0
            val min = current.stockMinimo.toIntOrNull() ?: 0
            val nuevoEstado = when {
                cant <= 0 -> ProductoEntity.ESTADO_AGOTADO
                cant <= min -> ProductoEntity.ESTADO_BAJO_STOCK
                else -> ProductoEntity.ESTADO_DISPONIBLE
            }
            current.copy(
                cantidad = filtrado,
                estado = nuevoEstado,
                cantidadError = null
            )
        }
    }

    fun onStockMinimoChange(value: String) {
        val filtrado = value.filter { it.isDigit() }
        _uiState.update { current ->
            val cant = current.cantidad.toIntOrNull() ?: 0
            val min = filtrado.toIntOrNull() ?: 0
            val nuevoEstado = when {
                cant <= 0 -> ProductoEntity.ESTADO_AGOTADO
                cant <= min -> ProductoEntity.ESTADO_BAJO_STOCK
                else -> ProductoEntity.ESTADO_DISPONIBLE
            }
            current.copy(
                stockMinimo = filtrado,
                estado = nuevoEstado,
                stockMinimoError = null
            )
        }
    }

    fun onPrecioCompraChange(value: String) {
        _uiState.update { it.copy(precioCompra = value, precioError = null) }
    }

    fun onPrecioVentaChange(value: String) {
        _uiState.update { it.copy(precioVenta = value, precioError = null) }
    }

    fun onUbicacionZonaChange(value: String) {
        _uiState.update { it.copy(ubicacionZona = value) }
    }

    fun onUbicacionEstanteChange(value: String) {
        _uiState.update { it.copy(ubicacionEstante = value) }
    }

    fun onUbicacionRepisaChange(value: String) {
        _uiState.update { it.copy(ubicacionRepisa = value) }
    }

    fun onProveedorNombreChange(value: String) {
        _uiState.update { it.copy(proveedorNombre = value) }
    }

    fun onEstadoChange(value: String) {
        _uiState.update { it.copy(estado = value) }
    }

    // --- Preparación de Navegación de Formulario ---

    fun prepararNuevoProducto() {
        _uiState.update {
            ProductoUiState(
                isEditing = false,
                codigo = "",
                nombre = "",
                marca = "",
                categoria = "Lubricantes",
                unidadMedida = "Unidad",
                cantidad = "0",
                stockMinimo = "5",
                precioCompra = "",
                precioVenta = "",
                ubicacionZona = "A",
                ubicacionEstante = "1",
                ubicacionRepisa = "A",
                proveedorNombre = "",
                estado = ProductoEntity.ESTADO_AGOTADO,
                productos = it.productos,
                searchQuery = _searchQuery.value,
                filtroCategoria = _categoriaFiltro.value,
                soloBajoStock = _soloBajoStock.value
            )
        }
    }

    fun cargarProductoParaEditar(codigo: String) {
        viewModelScope.launch {
            val producto = repository.getProductoByCodigo(codigo)
            if (producto != null) {
                _uiState.update { current ->
                    current.copy(
                        isEditing = true,
                        codigo = producto.codigo,
                        nombre = producto.nombre,
                        marca = producto.marca,
                        categoria = producto.categoria,
                        unidadMedida = producto.unidadMedida,
                        cantidad = producto.cantidad.toString(),
                        stockMinimo = producto.stockMinimo.toString(),
                        precioCompra = if (producto.precioCompra % 1.0 == 0.0) producto.precioCompra.toInt().toString() else producto.precioCompra.toString(),
                        precioVenta = if (producto.precioVenta % 1.0 == 0.0) producto.precioVenta.toInt().toString() else producto.precioVenta.toString(),
                        ubicacionZona = producto.ubicacionZona,
                        ubicacionEstante = producto.ubicacionEstante,
                        ubicacionRepisa = producto.ubicacionRepisa,
                        proveedorNombre = producto.proveedorNombre,
                        estado = producto.estado,
                        codigoError = null,
                        nombreError = null,
                        marcaError = null,
                        categoriaError = null,
                        cantidadError = null,
                        stockMinimoError = null,
                        precioError = null,
                        guardadoExitosamente = false
                    )
                }
            } else {
                _uiState.update { it.copy(errorMessage = "No se encontró el repuesto solicitado") }
            }
        }
    }

    // --- Validaciones Estrictas y Persistencia en Room ---

    fun validarYGuardar() {
        val state = _uiState.value

        var hasError = false
        var codigoErr: String? = null
        var nombreErr: String? = null
        var marcaErr: String? = null
        var categoriaErr: String? = null
        var cantidadErr: String? = null
        var stockMinErr: String? = null
        var precioErr: String? = null

        val codigoTrim = state.codigo.trim()
        val nombreTrim = state.nombre.trim()
        val marcaTrim = state.marca.trim()
        val categoriaTrim = state.categoria.trim()

        // 1. Validar no vacíos
        if (codigoTrim.isBlank()) {
            codigoErr = "El código de repuesto es obligatorio"
            hasError = true
        }

        if (nombreTrim.isBlank()) {
            nombreErr = "El nombre del producto es obligatorio"
            hasError = true
        }

        if (marcaTrim.isBlank()) {
            marcaErr = "La marca es obligatoria (ej. Bosch, Castrol)"
            hasError = true
        }

        if (categoriaTrim.isBlank()) {
            categoriaErr = "La categoría es obligatoria"
            hasError = true
        }

        // 2. Validar enteros >= 0
        val cantidadInt = state.cantidad.toIntOrNull()
        if (cantidadInt == null || cantidadInt < 0) {
            cantidadErr = "Debe ingresar una cantidad entera válida (≥ 0)"
            hasError = true
        }

        val stockMinInt = state.stockMinimo.toIntOrNull()
        if (stockMinInt == null || stockMinInt < 0) {
            stockMinErr = "Debe ingresar un stock mínimo válido (≥ 0)"
            hasError = true
        }

        // 3. Validar precios
        val precioCompraDouble = state.precioCompra.toDoubleOrNull()
        val precioVentaDouble = state.precioVenta.toDoubleOrNull()

        if (precioCompraDouble == null || precioCompraDouble < 0.0) {
            precioErr = "El precio de compra referencial debe ser ≥ 0"
            hasError = true
        } else if (precioVentaDouble == null || precioVentaDouble < 0.0) {
            precioErr = "El precio de venta referencial debe ser ≥ 0"
            hasError = true
        } else if (precioVentaDouble < precioCompraDouble) {
            precioErr = "El precio de venta ($precioVentaDouble) no puede ser menor al de compra ($precioCompraDouble)"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    codigoError = codigoErr,
                    nombreError = nombreErr,
                    marcaError = marcaErr,
                    categoriaError = categoriaErr,
                    cantidadError = cantidadErr,
                    stockMinimoError = stockMinErr,
                    precioError = precioErr
                )
            }
            return
        }

        // Validación de código duplicado en creación
        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true) }

            if (!state.isEditing) {
                val existente = repository.getProductoByCodigo(codigoTrim)
                if (existente != null) {
                    _uiState.update {
                        it.copy(
                            codigoError = "El código '$codigoTrim' ya existe en el inventario",
                            guardando = false
                        )
                    }
                    return@launch
                }
            }

            val cantFinal = cantidadInt ?: 0
            val stockMinFinal = stockMinInt ?: 0

            // Actualización automática de estado según cantidad y stock mínimo
            val estadoCalculado = when {
                cantFinal <= 0 -> ProductoEntity.ESTADO_AGOTADO
                cantFinal <= stockMinFinal -> ProductoEntity.ESTADO_BAJO_STOCK
                else -> ProductoEntity.ESTADO_DISPONIBLE
            }

            val entity = ProductoEntity(
                codigo = codigoTrim,
                nombre = nombreTrim,
                marca = marcaTrim,
                categoria = categoriaTrim,
                unidadMedida = state.unidadMedida.ifBlank { "Unidad" },
                cantidad = cantFinal,
                stockMinimo = stockMinFinal,
                precioCompra = precioCompraDouble ?: 0.0,
                precioVenta = precioVentaDouble ?: 0.0,
                ubicacionZona = state.ubicacionZona.ifBlank { "A" }.trim(),
                ubicacionEstante = state.ubicacionEstante.ifBlank { "1" }.trim(),
                ubicacionRepisa = state.ubicacionRepisa.ifBlank { "A" }.trim(),
                proveedorNombre = state.proveedorNombre.ifBlank { "Sin asignar" }.trim(),
                estado = estadoCalculado
            )

            repository.insertOrUpdate(entity)

            _uiState.update {
                it.copy(
                    guardando = false,
                    guardadoExitosamente = true
                )
            }
        }
    }

    fun resetGuardadoExitosamente() {
        _uiState.update { it.copy(guardadoExitosamente = false) }
    }

    // --- Eliminación de Productos ---

    fun solicitarEliminacion(producto: ProductoEntity?) {
        _uiState.update { it.copy(productoAEliminar = producto) }
    }

    fun confirmarEliminacion() {
        val producto = _uiState.value.productoAEliminar ?: return
        viewModelScope.launch {
            repository.delete(producto)
            _uiState.update { it.copy(productoAEliminar = null) }
        }
    }

    fun cancelarEliminacion() {
        _uiState.update { it.copy(productoAEliminar = null) }
    }

    // --- Escáner QR / Barras ---

    fun abrirEscaner(paraBusqueda: Boolean) {
        _uiState.update {
            it.copy(
                showScanner = true,
                isScanningForSearch = paraBusqueda
            )
        }
    }

    fun cerrarEscaner() {
        _uiState.update { it.copy(showScanner = false) }
    }

    fun onCodigoEscaneado(codigo: String) {
        val paraBusqueda = _uiState.value.isScanningForSearch
        _uiState.update { it.copy(showScanner = false) }

        if (paraBusqueda) {
            onSearchQueryChange(codigo)
        } else {
            onCodigoChange(codigo)
        }
    }

    fun limpiarMensajeError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

/**
 * Fábrica para instanciar ProductoViewModel con inyección manual de dependencias limpias.
 */
class ProductoViewModelFactory(
    private val repository: ProductoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoViewModel::class.java)) {
            return ProductoViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida: ${modelClass.name}")
    }
}

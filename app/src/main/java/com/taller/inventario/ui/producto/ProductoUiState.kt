package com.taller.inventario.ui.producto

import com.taller.inventario.data.local.ProductoEntity

/**
 * Estado inmutable de la UI para el módulo de inventario automotriz.
 * Contiene variables en String para enlace bidireccional limpio con formularios de Jetpack Compose,
 * errores independientes por campo y banderas de control reactivas.
 */
data class ProductoUiState(
    // Campos del formulario (Strings para edición)
    val codigo: String = "",
    val nombre: String = "",
    val marca: String = "",
    val categoria: String = "Lubricantes",
    val unidadMedida: String = "Unidad",
    val cantidad: String = "0",
    val stockMinimo: String = "5",
    val precioCompra: String = "",
    val precioVenta: String = "",
    val ubicacionZona: String = "A",
    val ubicacionEstante: String = "1",
    val ubicacionRepisa: String = "A",
    val proveedorNombre: String = "",
    val estado: String = ProductoEntity.ESTADO_DISPONIBLE,

    // Mensajes de error independientes por campo
    val codigoError: String? = null,
    val nombreError: String? = null,
    val marcaError: String? = null,
    val categoriaError: String? = null,
    val cantidadError: String? = null,
    val stockMinimoError: String? = null,
    val precioError: String? = null,

    // Banderas de control y flujo
    val isEditing: Boolean = false,
    val guardando: Boolean = false,
    val guardadoExitosamente: Boolean = false,
    val searchQuery: String = "",
    val filtroCategoria: String = "Todas",
    val soloBajoStock: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // Colecciones y selección
    val productos: List<ProductoEntity> = emptyList(),
    val productoAEliminar: ProductoEntity? = null,

    // Control de escáner QR / Barras
    val showScanner: Boolean = false,
    val isScanningForSearch: Boolean = false
) {
    /**
     * Cálculo en tiempo real de la reposición sugerida para el formulario activo.
     */
    val cantidadSugeridaReposicionCalculada: Int
        get() {
            val cant = cantidad.toIntOrNull() ?: 0
            val min = stockMinimo.toIntOrNull() ?: 0
            return maxOf(0, (min * 2) - cant)
        }

    /**
     * Retorna verdadero si hay algún error de validación activo en el formulario.
     */
    val hasErrors: Boolean
        get() = codigoError != null ||
                nombreError != null ||
                marcaError != null ||
                categoriaError != null ||
                cantidadError != null ||
                stockMinimoError != null ||
                precioError != null
}

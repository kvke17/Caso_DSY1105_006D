package com.taller.inventario.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa un producto/repuesto en el inventario del taller automotriz.
 * Cumple con los requerimientos de la asignatura DSY1105 - Duoc UC.
 */
@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey
    val codigo: String,
    val nombre: String,
    val marca: String,
    val categoria: String,
    val unidadMedida: String,
    val cantidad: Int,
    val stockMinimo: Int,
    val precioCompra: Double,
    val precioVenta: Double,
    val ubicacionZona: String,
    val ubicacionEstante: String,
    val ubicacionRepisa: String,
    val proveedorNombre: String,
    val estado: String
) {
    /**
     * Regla de negocio: Reposición automática mínima.
     * Calcula la cantidad recomendada a comprar para mantener stock de seguridad.
     */
    val cantidadSugeridaReposicion: Int
        get() = maxOf(0, (stockMinimo * 2) - cantidad)

    /**
     * Ubicación formateada para lectura rápida en bodega de taller.
     */
    val ubicacionCompleta: String
        get() = "Zona $ubicacionZona • Estante $ubicacionEstante • Repisa $ubicacionRepisa"

    /**
     * Retorna el estado coherente con los niveles de stock actuales.
     */
    fun resolverEstadoStock(): String {
        return when {
            cantidad <= 0 -> ESTADO_AGOTADO
            cantidad <= stockMinimo -> ESTADO_BAJO_STOCK
            else -> ESTADO_DISPONIBLE
        }
    }

    companion object {
        const val ESTADO_DISPONIBLE = "DISPONIBLE"
        const val ESTADO_BAJO_STOCK = "BAJO_STOCK"
        const val ESTADO_AGOTADO = "AGOTADO"
        const val ESTADO_ACTIVO = "ACTIVO"
        const val ESTADO_INACTIVO = "INACTIVO"

        val UNIDADES_MEDIDA = listOf("Unidad", "Litros", "Bidón", "Kit", "Juego", "Caja")
        val CATEGORIAS = listOf("Lubricantes", "Filtros", "Frenos", "Encendido", "Baterías", "Suspensión", "Neumáticos", "Accesorios")
    }
}

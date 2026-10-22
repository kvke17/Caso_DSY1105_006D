package com.taller.inventario

import com.taller.inventario.data.local.ProductoEntity
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pruebas unitarias de las reglas de negocio del caso de inventario automotriz (DSY1105).
 */
class ProductoEntityTest {

    @Test
    fun `regla de reposicion automatica sugiere el doble del minimo menos la cantidad`() {
        val productoBajoStock = ProductoEntity(
            codigo = "TEST-01",
            nombre = "Filtro de Aceite",
            marca = "Mann Filter",
            categoria = "Filtros",
            unidadMedida = "Unidad",
            cantidad = 3,
            stockMinimo = 6, // 6 * 2 - 3 = 9
            precioCompra = 5000.0,
            precioVenta = 8000.0,
            ubicacionZona = "A",
            ubicacionEstante = "1",
            ubicacionRepisa = "A",
            proveedorNombre = "Proveedor Test",
            estado = ProductoEntity.ESTADO_BAJO_STOCK
        )

        assertEquals(9, productoBajoStock.cantidadSugeridaReposicion)
    }

    @Test
    fun `regla de reposicion retorna cero cuando el inventario supera el doble del stock minimo`() {
        val productoConSobrestock = ProductoEntity(
            codigo = "TEST-02",
            nombre = "Aceite Motor 10W40",
            marca = "Castrol",
            categoria = "Lubricantes",
            unidadMedida = "Bidón",
            cantidad = 20,
            stockMinimo = 5, // 5 * 2 - 20 = -10 -> maxOf(0, -10) = 0
            precioCompra = 20000.0,
            precioVenta = 30000.0,
            ubicacionZona = "B",
            ubicacionEstante = "2",
            ubicacionRepisa = "B",
            proveedorNombre = "Proveedor Test",
            estado = ProductoEntity.ESTADO_DISPONIBLE
        )

        assertEquals(0, productoConSobrestock.cantidadSugeridaReposicion)
    }

    @Test
    fun `resolver estado stock clasifica correctamente en AGOTADO, BAJO_STOCK y DISPONIBLE`() {
        val base = ProductoEntity(
            codigo = "TEST-03",
            nombre = "Bujía Test",
            marca = "Bosch",
            categoria = "Encendido",
            unidadMedida = "Juego",
            cantidad = 0,
            stockMinimo = 4,
            precioCompra = 10000.0,
            precioVenta = 15000.0,
            ubicacionZona = "A",
            ubicacionEstante = "1",
            ubicacionRepisa = "A",
            proveedorNombre = "Proveedor Test",
            estado = ""
        )

        assertEquals(ProductoEntity.ESTADO_AGOTADO, base.copy(cantidad = 0).resolverEstadoStock())
        assertEquals(ProductoEntity.ESTADO_BAJO_STOCK, base.copy(cantidad = 4).resolverEstadoStock())
        assertEquals(ProductoEntity.ESTADO_BAJO_STOCK, base.copy(cantidad = 2).resolverEstadoStock())
        assertEquals(ProductoEntity.ESTADO_DISPONIBLE, base.copy(cantidad = 5).resolverEstadoStock())
    }

    @Test
    fun `ubicacion detallada de bodega formatea correctamente zona, estante y repisa`() {
        val producto = ProductoEntity(
            codigo = "TEST-04",
            nombre = "Pastillas de freno",
            marca = "Brembo",
            categoria = "Frenos",
            unidadMedida = "Kit",
            cantidad = 10,
            stockMinimo = 4,
            precioCompra = 30000.0,
            precioVenta = 45000.0,
            ubicacionZona = "Sector Norte",
            ubicacionEstante = "Est-04",
            ubicacionRepisa = "Nivel-2",
            proveedorNombre = "Proveedor Test",
            estado = ProductoEntity.ESTADO_DISPONIBLE
        )

        assertEquals("Zona Sector Norte • Estante Est-04 • Repisa Nivel-2", producto.ubicacionCompleta)
    }
}

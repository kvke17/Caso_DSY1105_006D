package com.taller.inventario

import com.taller.inventario.data.local.ProductoDao
import com.taller.inventario.data.local.ProductoEntity
import com.taller.inventario.data.remote.ExternalApiService
import com.taller.inventario.data.remote.IndicadorItem
import com.taller.inventario.data.remote.InventarioApiService
import com.taller.inventario.data.remote.MindicadorResponse
import com.taller.inventario.data.repository.ProductoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

/**
 * Pruebas unitarias de ProductoRepository validando:
 * - CRUD híbrido (Room local + Spring Boot remoto)
 * - Manejo de fallos de red y modo offline
 * - Detección de stock crítico
 * - Consumo de API externa de indicadores económicos
 */
class ProductoRepositoryTest {

    private lateinit var productoDao: ProductoDao
    private lateinit var apiService: InventarioApiService
    private lateinit var externalApiService: ExternalApiService
    private lateinit var repository: ProductoRepository

    private val productoEjemplo = ProductoEntity(
        codigo = "REP-001",
        nombre = "Pastillas de Freno Delanteras",
        marca = "Brembo",
        categoria = "Frenos",
        unidadMedida = "Juego",
        cantidad = 3,
        stockMinimo = 5,
        precioCompra = 25000.0,
        precioVenta = 38000.0,
        ubicacionZona = "A",
        ubicacionEstante = "1",
        ubicacionRepisa = "A",
        proveedorNombre = "Brembo Chile",
        estado = ProductoEntity.ESTADO_BAJO_STOCK
    )

    @Before
    fun setup() {
        productoDao = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        externalApiService = mockk(relaxed = true)
        repository = ProductoRepository(productoDao, apiService, externalApiService)
    }

    @Test
    fun `getAllProductos retorna flujo emitido por Room DAO`() = runTest {
        val lista = listOf(productoEjemplo)
        every { productoDao.getAllProductos() } returns flowOf(lista)

        val resultado = repository.getAllProductos().first()

        assertEquals(1, resultado.size)
        assertEquals("REP-001", resultado[0].codigo)
        coVerify(exactly = 1) { productoDao.getAllProductos() }
    }

    @Test
    fun `getProductoByCodigo retorna desde Room si existe localmente sin consultar backend`() = runTest {
        coEvery { productoDao.getProductoByCodigo("REP-001") } returns productoEjemplo

        val resultado = repository.getProductoByCodigo("REP-001")

        assertNotNull(resultado)
        assertEquals("Brembo", resultado?.marca)
        coVerify(exactly = 0) { apiService.getProductoById(any()) }
    }

    @Test
    fun `getProductoByCodigo consulta backend y cachea en Room si no existe localmente`() = runTest {
        coEvery { productoDao.getProductoByCodigo("REP-001") } returns null
        coEvery { apiService.getProductoById("REP-001") } returns productoEjemplo

        val resultado = repository.getProductoByCodigo("REP-001")

        assertNotNull(resultado)
        assertEquals("REP-001", resultado?.codigo)
        coVerify(exactly = 1) { apiService.getProductoById("REP-001") }
        coVerify(exactly = 1) { productoDao.insertOrUpdate(productoEjemplo) }
    }

    @Test
    fun `getProductoByCodigo maneja fallo de red retornando null cuando no existe en Room ni remoto`() = runTest {
        coEvery { productoDao.getProductoByCodigo("REP-001") } returns null
        coEvery { apiService.getProductoById("REP-001") } throws IOException("Backend offline")

        val resultado = repository.getProductoByCodigo("REP-001")

        assertNull(resultado)
    }

    @Test
    fun `insertOrUpdate guarda en Room e impacta en Spring Boot en creacion`() = runTest {
        coEvery { apiService.createProducto(productoEjemplo) } returns productoEjemplo

        repository.insertOrUpdate(productoEjemplo, isEditing = false)

        coVerify(exactly = 1) { productoDao.insertOrUpdate(productoEjemplo) }
        coVerify(exactly = 1) { apiService.createProducto(productoEjemplo) }
    }

    @Test
    fun `insertOrUpdate guarda en Room e impacta en Spring Boot en edicion`() = runTest {
        coEvery { apiService.updateProducto("REP-001", productoEjemplo) } returns productoEjemplo

        repository.insertOrUpdate(productoEjemplo, isEditing = true)

        coVerify(exactly = 1) { productoDao.insertOrUpdate(productoEjemplo) }
        coVerify(exactly = 1) { apiService.updateProducto("REP-001", productoEjemplo) }
    }

    @Test
    fun `insertOrUpdate maneja fallo de red en Spring Boot guardando exitosamente en Room`() = runTest {
        coEvery { apiService.createProducto(any()) } throws IOException("Conexión rehusada")
        coEvery { apiService.updateProducto(any(), any()) } throws IOException("Conexión rehusada")

        repository.insertOrUpdate(productoEjemplo, isEditing = false)

        coVerify(exactly = 1) { productoDao.insertOrUpdate(productoEjemplo) }
    }

    @Test
    fun `delete elimina en Room y en Spring Boot`() = runTest {
        coEvery { apiService.deleteProducto("REP-001") } returns Response.success(Unit)

        repository.delete(productoEjemplo)

        coVerify(exactly = 1) { productoDao.delete(productoEjemplo) }
        coVerify(exactly = 1) { apiService.deleteProducto("REP-001") }
    }

    @Test
    fun `delete maneja fallo de red en Spring Boot completando eliminacion en Room`() = runTest {
        coEvery { apiService.deleteProducto(any()) } throws IOException("Servidor no disponible")

        repository.delete(productoEjemplo)

        coVerify(exactly = 1) { productoDao.delete(productoEjemplo) }
    }

    @Test
    fun `getProductosBajoStock consulta DAO para obtener repuestos con stock critico`() = runTest {
        val repuestosBajos = listOf(productoEjemplo)
        every { productoDao.getProductosBajoStock() } returns flowOf(repuestosBajos)

        val resultado = repository.getProductosBajoStock().first()

        assertEquals(1, resultado.size)
        assertTrue(resultado[0].cantidad <= resultado[0].stockMinimo)
    }

    @Test
    fun `sincronizarDesdeRemoto descarga productos y actualiza Room correctamente`() = runTest {
        val listaRemota = listOf(productoEjemplo)
        coEvery { apiService.getProductos() } returns listaRemota

        val resultado = repository.sincronizarDesdeRemoto()

        assertTrue(resultado.isSuccess)
        assertEquals(1, resultado.getOrNull()?.size)
        coVerify(exactly = 1) { productoDao.insertAll(listaRemota) }
    }

    @Test
    fun `sincronizarDesdeRemoto captura excepcion de red y retorna failure`() = runTest {
        coEvery { apiService.getProductos() } throws IOException("Error 500")

        val resultado = repository.sincronizarDesdeRemoto()

        assertTrue(resultado.isFailure)
        coVerify(exactly = 0) { productoDao.insertAll(any()) }
    }

    @Test
    fun `getIndicadoresEconomicos obtiene Dolar y UF correctamente desde API externa`() = runTest {
        val respuesta = MindicadorResponse(
            dolar = IndicadorItem(codigo = "dolar", nombre = "Dólar", valor = 980.50),
            uf = IndicadorItem(codigo = "uf", nombre = "UF", valor = 37800.25)
        )
        coEvery { externalApiService.getIndicadores() } returns respuesta

        val resultado = repository.getIndicadoresEconomicos()

        assertNotNull(resultado)
        assertEquals(980.50, resultado?.dolar?.valor ?: 0.0, 0.001)
        assertEquals(37800.25, resultado?.uf?.valor ?: 0.0, 0.001)
    }

    @Test
    fun `getIndicadoresEconomicos maneja fallo de red retornando null sin lanzar excepcion`() = runTest {
        coEvery { externalApiService.getIndicadores() } throws IOException("Sin conexión a mindicador.cl")

        val resultado = repository.getIndicadoresEconomicos()

        assertNull(resultado)
    }
}

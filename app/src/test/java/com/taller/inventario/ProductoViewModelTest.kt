package com.taller.inventario

import com.taller.inventario.data.local.ProductoEntity
import com.taller.inventario.data.remote.IndicadorItem
import com.taller.inventario.data.remote.MindicadorResponse
import com.taller.inventario.data.repository.ProductoRepository
import com.taller.inventario.ui.producto.ProductoViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para ProductoViewModel validando:
 * - Flujo reactivo MVVM y filtrado por búsqueda / categoría / stock crítico
 * - Validaciones de formulario (campos vacíos, cantidades enteras, coherencia de precios)
 * - Actualización dinámica del estado del repuesto (AGOTADO, BAJO_STOCK, DISPONIBLE)
 * - Manejo de datos y errores al consumir la API externa (Dólar / UF)
 * - Flujo de persistencia híbrida (creación, edición y eliminación)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProductoViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: ProductoRepository
    private lateinit var viewModel: ProductoViewModel

    private val producto1 = ProductoEntity(
        codigo = "ACE-001",
        nombre = "Aceite Sintético 5W30",
        marca = "Castrol",
        categoria = "Lubricantes",
        unidadMedida = "Bidón",
        cantidad = 10,
        stockMinimo = 5,
        precioCompra = 20000.0,
        precioVenta = 32000.0,
        ubicacionZona = "A",
        ubicacionEstante = "1",
        ubicacionRepisa = "A",
        proveedorNombre = "Castrol Chile",
        estado = ProductoEntity.ESTADO_DISPONIBLE
    )

    private val producto2 = ProductoEntity(
        codigo = "FIL-002",
        nombre = "Filtro de Aire",
        marca = "Bosch",
        categoria = "Filtros",
        unidadMedida = "Unidad",
        cantidad = 2,
        stockMinimo = 4,
        precioCompra = 5000.0,
        precioVenta = 9000.0,
        ubicacionZona = "B",
        ubicacionEstante = "2",
        ubicacionRepisa = "A",
        proveedorNombre = "Bosch Repuestos",
        estado = ProductoEntity.ESTADO_BAJO_STOCK
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        every { repository.getAllProductos() } returns flowOf(listOf(producto1, producto2))
        coEvery { repository.getIndicadoresEconomicos() } returns MindicadorResponse(
            dolar = IndicadorItem(codigo = "dolar", nombre = "Dólar", valor = 980.0),
            uf = IndicadorItem(codigo = "uf", nombre = "UF", valor = 37900.0)
        )

        viewModel = ProductoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `inicializacion carga productos reactivamente e indicadores externos`() = runTest {
        val state = viewModel.uiState.value

        assertEquals(2, state.productos.size)
        assertEquals(980.0, state.valorDolar ?: 0.0, 0.01)
        assertEquals(37900.0, state.valorUf ?: 0.0, 0.01)
        assertFalse(state.isLoading)
    }

    @Test
    fun `onSearchQueryChange filtra productos por nombre o codigo`() = runTest {
        viewModel.onSearchQueryChange("Bosch")

        val state = viewModel.uiState.value
        assertEquals(1, state.productos.size)
        assertEquals("FIL-002", state.productos[0].codigo)
    }

    @Test
    fun `onFiltroCategoriaChange filtra correctamente por categoria automotriz`() = runTest {
        viewModel.onFiltroCategoriaChange("Lubricantes")

        val state = viewModel.uiState.value
        assertEquals(1, state.productos.size)
        assertEquals("Lubricantes", state.productos[0].categoria)
    }

    @Test
    fun `toggleSoloBajoStock aisla repuestos con stock critico`() = runTest {
        viewModel.toggleSoloBajoStock()

        val state = viewModel.uiState.value
        assertTrue(state.soloBajoStock)
        assertEquals(1, state.productos.size)
        assertEquals("FIL-002", state.productos[0].codigo)
        assertTrue(state.productos[0].cantidad <= state.productos[0].stockMinimo)
    }

    @Test
    fun `onCantidadChange actualiza estado automatico a AGOTADO cuando cantidad es cero`() = runTest {
        viewModel.onCantidadChange("0")
        assertEquals(ProductoEntity.ESTADO_AGOTADO, viewModel.uiState.value.estado)
    }

    @Test
    fun `onCantidadChange actualiza estado automatico a BAJO_STOCK cuando es menor o igual al minimo`() = runTest {
        viewModel.onStockMinimoChange("10")
        viewModel.onCantidadChange("5")
        assertEquals(ProductoEntity.ESTADO_BAJO_STOCK, viewModel.uiState.value.estado)
    }

    @Test
    fun `onCantidadChange actualiza estado automatico a DISPONIBLE cuando supera el stock minimo`() = runTest {
        viewModel.onStockMinimoChange("5")
        viewModel.onCantidadChange("15")
        assertEquals(ProductoEntity.ESTADO_DISPONIBLE, viewModel.uiState.value.estado)
    }

    @Test
    fun `validarYGuardar con campos obligatorios vacios reporta errores sin invocar repository`() = runTest {
        viewModel.prepararNuevoProducto()
        viewModel.validarYGuardar()

        val state = viewModel.uiState.value
        assertTrue(state.hasErrors)
        assertNotNull(state.codigoError)
        assertNotNull(state.nombreError)
        assertNotNull(state.marcaError)
        assertFalse(state.guardadoExitosamente)
        coVerify(exactly = 0) { repository.insertOrUpdate(any(), any()) }
    }

    @Test
    fun `validarYGuardar con precio de venta menor al de compra reporta error de margen comercial`() = runTest {
        viewModel.prepararNuevoProducto()
        viewModel.onCodigoChange("PR-999")
        viewModel.onNombreChange("Pastillas")
        viewModel.onMarcaChange("Brembo")
        viewModel.onPrecioCompraChange("20000")
        viewModel.onPrecioVentaChange("15000") // menor que compra

        viewModel.validarYGuardar()

        val state = viewModel.uiState.value
        assertNotNull(state.precioError)
        coVerify(exactly = 0) { repository.insertOrUpdate(any(), any()) }
    }

    @Test
    fun `validarYGuardar con datos validos invoca repository y marca guardadoExitosamente`() = runTest {
        coEvery { repository.getProductoByCodigo("PR-100") } returns null

        viewModel.prepararNuevoProducto()
        viewModel.onCodigoChange("PR-100")
        viewModel.onNombreChange("Filtro de Aceite")
        viewModel.onMarcaChange("Mann")
        viewModel.onCategoriaChange("Filtros")
        viewModel.onCantidadChange("10")
        viewModel.onStockMinimoChange("4")
        viewModel.onPrecioCompraChange("5000")
        viewModel.onPrecioVentaChange("9500")

        viewModel.validarYGuardar()

        val state = viewModel.uiState.value
        assertFalse(state.hasErrors)
        assertTrue(state.guardadoExitosamente)
        coVerify(exactly = 1) { repository.insertOrUpdate(any(), isEditing = false) }
    }

    @Test
    fun `validarYGuardar en creacion rechaza codigo duplicado si ya existe en inventario`() = runTest {
        coEvery { repository.getProductoByCodigo("ACE-001") } returns producto1

        viewModel.prepararNuevoProducto()
        viewModel.onCodigoChange("ACE-001")
        viewModel.onNombreChange("Aceite Nuevo")
        viewModel.onMarcaChange("Castrol")
        viewModel.onPrecioCompraChange("10000")
        viewModel.onPrecioVentaChange("15000")

        viewModel.validarYGuardar()

        val state = viewModel.uiState.value
        assertNotNull(state.codigoError)
        assertFalse(state.guardadoExitosamente)
        coVerify(exactly = 0) { repository.insertOrUpdate(any(), any()) }
    }

    @Test
    fun `cargarProductoParaEditar carga datos del repuesto en el formulario`() = runTest {
        coEvery { repository.getProductoByCodigo("ACE-001") } returns producto1

        viewModel.cargarProductoParaEditar("ACE-001")

        val state = viewModel.uiState.value
        assertTrue(state.isEditing)
        assertEquals("ACE-001", state.codigo)
        assertEquals("Aceite Sintético 5W30", state.nombre)
        assertEquals("Castrol", state.marca)
        assertNull(state.codigoError)
    }

    @Test
    fun `confirmarEliminacion invoca repository delete y limpia producto seleccionado`() = runTest {
        viewModel.solicitarEliminacion(producto1)
        assertEquals(producto1, viewModel.uiState.value.productoAEliminar)

        viewModel.confirmarEliminacion()

        assertNull(viewModel.uiState.value.productoAEliminar)
        coVerify(exactly = 1) { repository.delete(producto1) }
    }

    @Test
    fun `cargarIndicadores maneja fallo de red en API externa manteniendo valores seguros`() = runTest {
        coEvery { repository.getIndicadoresEconomicos() } returns null

        viewModel.cargarIndicadores()

        val state = viewModel.uiState.value
        assertFalse(state.indicadoresCargando)
    }

    @Test
    fun `onCodigoEscaneado actualiza busqueda cuando se escanea para busqueda`() = runTest {
        viewModel.abrirEscaner(paraBusqueda = true)
        viewModel.onCodigoEscaneado("FIL-002")

        assertEquals("FIL-002", viewModel.uiState.value.searchQuery)
        assertFalse(viewModel.uiState.value.showScanner)
    }

    @Test
    fun `onCodigoEscaneado actualiza codigo de formulario cuando se escanea para registro`() = runTest {
        viewModel.abrirEscaner(paraBusqueda = false)
        viewModel.onCodigoEscaneado("NUEVO-COD-99")

        assertEquals("NUEVO-COD-99", viewModel.uiState.value.codigo)
        assertFalse(viewModel.uiState.value.showScanner)
    }
}

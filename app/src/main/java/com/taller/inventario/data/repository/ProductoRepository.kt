package com.taller.inventario.data.repository

import com.taller.inventario.data.local.AppDatabase
import com.taller.inventario.data.local.ProductoDao
import com.taller.inventario.data.local.ProductoEntity
import com.taller.inventario.data.remote.ExternalApiService
import com.taller.inventario.data.remote.InventarioApiService
import com.taller.inventario.data.remote.MindicadorResponse
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de la capa de datos que implementa Clean Architecture con sincronización híbrida:
 * - Room Database como fuente de la verdad local, caché y respaldo offline.
 * - Microservicio Spring Boot como backend remoto en tiempo real.
 * - API externa mindicador.cl para cotizaciones referenciales de repuestos importados.
 */
class ProductoRepository(
    private val productoDao: ProductoDao,
    private val apiService: InventarioApiService = InventarioApiService.create(),
    private val externalApiService: ExternalApiService = ExternalApiService.create()
) {
    fun getAllProductos(): Flow<List<ProductoEntity>> {
        return productoDao.getAllProductos()
    }

    suspend fun getProductoByCodigo(codigo: String): ProductoEntity? {
        val local = productoDao.getProductoByCodigo(codigo.trim())
        if (local != null) return local

        return try {
            val remoto = apiService.getProductoById(codigo.trim())
            productoDao.insertOrUpdate(remoto)
            remoto
        } catch (_: Exception) {
            null
        }
    }

    suspend fun insertOrUpdate(producto: ProductoEntity, isEditing: Boolean = false) {
        // 1. Guardar siempre en Room local
        productoDao.insertOrUpdate(producto)

        // 2. Intentar impactar en Spring Boot remoto en tiempo real
        try {
            if (isEditing) {
                apiService.updateProducto(producto.codigo, producto)
            } else {
                apiService.createProducto(producto)
            }
        } catch (_: Exception) {
            try {
                if (isEditing) {
                    apiService.createProducto(producto)
                } else {
                    apiService.updateProducto(producto.codigo, producto)
                }
            } catch (_: Exception) {
                // Backend desconectado: Room actúa como respaldo local
            }
        }
    }

    suspend fun delete(producto: ProductoEntity) {
        productoDao.delete(producto)
        try {
            apiService.deleteProducto(producto.codigo)
        } catch (_: Exception) {
            // Backend desconectado: Room actúa como respaldo local
        }
    }

    fun searchProductos(query: String): Flow<List<ProductoEntity>> {
        return if (query.isBlank()) {
            productoDao.getAllProductos()
        } else {
            productoDao.searchProductos(query.trim())
        }
    }

    fun getProductosBajoStock(): Flow<List<ProductoEntity>> {
        return productoDao.getProductosBajoStock()
    }

    fun getProductosByCategoria(categoria: String): Flow<List<ProductoEntity>> {
        return productoDao.getProductosByCategoria(categoria)
    }

    suspend fun sincronizarDesdeRemoto(): Result<List<ProductoEntity>> {
        return try {
            val remotos = apiService.getProductos()
            if (remotos.isNotEmpty()) {
                productoDao.insertAll(remotos)
            }
            Result.success(remotos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verificarYPrecargarSiVacio() {
        val sincronizado = try {
            val remotos = apiService.getProductos()
            if (remotos.isNotEmpty()) {
                productoDao.insertAll(remotos)
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }

        if (!sincronizado && productoDao.getCount() < 10) {
            AppDatabase.prepopulateDatabase(productoDao)
        }
    }

    suspend fun getIndicadoresEconomicos(): MindicadorResponse? {
        return try {
            externalApiService.getIndicadores()
        } catch (_: Exception) {
            null
        }
    }
}

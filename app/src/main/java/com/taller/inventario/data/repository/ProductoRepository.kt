package com.taller.inventario.data.repository

import com.taller.inventario.data.local.AppDatabase
import com.taller.inventario.data.local.ProductoDao
import com.taller.inventario.data.local.ProductoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de la capa de datos que implementa Clean Architecture.
 * Desacopla el acceso a Room Database de los ViewModels.
 */
class ProductoRepository(
    private val productoDao: ProductoDao
) {
    fun getAllProductos(): Flow<List<ProductoEntity>> {
        return productoDao.getAllProductos()
    }

    suspend fun getProductoByCodigo(codigo: String): ProductoEntity? {
        return productoDao.getProductoByCodigo(codigo.trim())
    }

    suspend fun insertOrUpdate(producto: ProductoEntity) {
        productoDao.insertOrUpdate(producto)
    }

    suspend fun delete(producto: ProductoEntity) {
        productoDao.delete(producto)
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

    suspend fun verificarYPrecargarSiVacio() {
        if (productoDao.getCount() < 10) {
            AppDatabase.prepopulateDatabase(productoDao)
        }
    }
}

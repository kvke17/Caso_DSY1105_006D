package com.taller.inventario

import android.app.Application
import com.taller.inventario.data.local.AppDatabase
import com.taller.inventario.data.repository.ProductoRepository

/**
 * Clase Application principal que inicializa el contenedor de dependencias del taller automotriz.
 */
class InventarioApp : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val repository: ProductoRepository by lazy {
        ProductoRepository(database.productoDao())
    }
}

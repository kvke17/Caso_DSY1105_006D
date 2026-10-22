package com.taller.inventario.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para operaciones sobre la tabla 'productos'.
 */
@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAllProductos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE codigo = :codigo LIMIT 1")
    suspend fun getProductoByCodigo(codigo: String): ProductoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(producto: ProductoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productos: List<ProductoEntity>)

    @Delete
    suspend fun delete(producto: ProductoEntity)

    @Query("""
        SELECT * FROM productos 
        WHERE nombre LIKE '%' || :query || '%' 
           OR codigo LIKE '%' || :query || '%' 
           OR marca LIKE '%' || :query || '%' 
           OR categoria LIKE '%' || :query || '%'
        ORDER BY nombre ASC
    """)
    fun searchProductos(query: String): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE cantidad <= stockMinimo ORDER BY cantidad ASC")
    fun getProductosBajoStock(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE categoria = :categoria ORDER BY nombre ASC")
    fun getProductosByCategoria(categoria: String): Flow<List<ProductoEntity>>

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun getCount(): Int
}

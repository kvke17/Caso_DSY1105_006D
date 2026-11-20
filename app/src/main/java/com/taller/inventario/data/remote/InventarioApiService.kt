package com.taller.inventario.data.remote

import com.taller.inventario.data.local.ProductoEntity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

/**
 * Servicio Retrofit para comunicación con el microservicio Spring Boot del inventario.
 * Apunta a http://10.0.2.2:8080/api/productos/ (alias del localhost del host desde el emulador Android).
 */
interface InventarioApiService {

    @GET(".")
    suspend fun getProductos(): List<ProductoEntity>

    @GET("{id}")
    suspend fun getProductoById(@Path("id") id: String): ProductoEntity

    @POST(".")
    suspend fun createProducto(@Body producto: ProductoEntity): ProductoEntity

    @PUT("{id}")
    suspend fun updateProducto(
        @Path("id") id: String,
        @Body producto: ProductoEntity
    ): ProductoEntity

    @DELETE("{id}")
    suspend fun deleteProducto(@Path("id") id: String): Response<Unit>

    companion object {
        const val BASE_URL = "http://10.0.2.2:8080/api/productos/"

        fun create(baseUrl: String = BASE_URL): InventarioApiService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(InventarioApiService::class.java)
        }
    }
}

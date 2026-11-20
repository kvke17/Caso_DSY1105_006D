package com.taller.inventario.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

/**
 * Modelos para deserializar la respuesta de https://mindicador.cl/api/
 */
data class IndicadorItem(
    val codigo: String = "",
    val nombre: String = "",
    val unidad_medida: String = "",
    val fecha: String = "",
    val valor: Double = 0.0
)

data class MindicadorResponse(
    val version: String? = null,
    val autor: String? = null,
    val fecha: String? = null,
    val uf: IndicadorItem? = null,
    val dolar: IndicadorItem? = null,
    val euro: IndicadorItem? = null,
    val utm: IndicadorItem? = null
)

/**
 * Servicio Retrofit para consumir la API pública de indicadores económicos de Chile (mindicador.cl).
 * Utilizado para cotización de referencia en repuestos importados (Dólar y UF).
 */
interface ExternalApiService {

    @GET("api")
    suspend fun getIndicadores(): MindicadorResponse

    companion object {
        const val BASE_URL = "https://mindicador.cl/"

        fun create(baseUrl: String = BASE_URL): ExternalApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ExternalApiService::class.java)
        }
    }
}

package com.taller.inventario.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Base de datos principal de la aplicación mediante Room Database.
 * Implementa el patrón Singleton y precarga productos ficticios representativos.
 */
@Database(
    entities = [ProductoEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventario_taller_automotriz.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        prepopulateDatabase(database.productoDao())
                    }
                }
            }

            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        prepopulateDatabase(database.productoDao())
                    }
                }
            }
        }

        /**
         * Precarga de datos iniciales representativos de un taller automotriz en Puente Alto.
         * Incluye catálogo ampliado de repuestos: aceites, filtros, frenos, baterías, bujías,
         * suspensión, neumáticos y kits de distribución con diversos niveles de stock.
         */
        suspend fun prepopulateDatabase(dao: ProductoDao) {
            val productosIniciales = listOf(
                ProductoEntity(
                    codigo = "ACE-CAST-5W30",
                    nombre = "Aceite Sintético Edge 5W-30 4L",
                    marca = "Castrol",
                    categoria = "Lubricantes",
                    unidadMedida = "Bidón",
                    cantidad = 16,
                    stockMinimo = 6,
                    precioCompra = 27990.0,
                    precioVenta = 38990.0,
                    ubicacionZona = "A",
                    ubicacionEstante = "1",
                    ubicacionRepisa = "A",
                    proveedorNombre = "Lubricentro Central SpA",
                    estado = ProductoEntity.ESTADO_DISPONIBLE
                ),
                ProductoEntity(
                    codigo = "ACE-MOTUL-8100",
                    nombre = "Aceite Sintético 8100 X-cess 5W-40 5L",
                    marca = "Motul",
                    categoria = "Lubricantes",
                    unidadMedida = "Bidón",
                    cantidad = 12,
                    stockMinimo = 5,
                    precioCompra = 34500.0,
                    precioVenta = 48990.0,
                    ubicacionZona = "A",
                    ubicacionEstante = "1",
                    ubicacionRepisa = "B",
                    proveedorNombre = "Lubricentro Central SpA",
                    estado = ProductoEntity.ESTADO_DISPONIBLE
                ),
                ProductoEntity(
                    codigo = "ACE-VALV-ATF",
                    nombre = "Fluido Transmisión Automática MaxLife ATF 1L",
                    marca = "Valvoline",
                    categoria = "Lubricantes",
                    unidadMedida = "Unidad",
                    cantidad = 8,
                    stockMinimo = 6,
                    precioCompra = 8900.0,
                    precioVenta = 14500.0,
                    ubicacionZona = "A",
                    ubicacionEstante = "2",
                    ubicacionRepisa = "C",
                    proveedorNombre = "Lubricentro Central SpA",
                    estado = ProductoEntity.ESTADO_DISPONIBLE
                ),
                ProductoEntity(
                    codigo = "FLT-MANN-HU711",
                    nombre = "Filtro de Aceite Blindado HU 711/51 x",
                    marca = "Mann Filter",
                    categoria = "Filtros",
                    unidadMedida = "Unidad",
                    cantidad = 3,
                    stockMinimo = 6,
                    precioCompra = 6800.0,
                    precioVenta = 11990.0,
                    ubicacionZona = "B",
                    ubicacionEstante = "2",
                    ubicacionRepisa = "B",
                    proveedorNombre = "Repuestos Chile SpA",
                    estado = ProductoEntity.ESTADO_BAJO_STOCK
                ),
                ProductoEntity(
                    codigo = "FLT-BOSCH-1987",
                    nombre = "Filtro de Aire Motor F026400002",
                    marca = "Bosch",
                    categoria = "Filtros",
                    unidadMedida = "Unidad",
                    cantidad = 14,
                    stockMinimo = 5,
                    precioCompra = 8900.0,
                    precioVenta = 15990.0,
                    ubicacionZona = "B",
                    ubicacionEstante = "1",
                    ubicacionRepisa = "A",
                    proveedorNombre = "Distribuidora Automotriz Sur",
                    estado = ProductoEntity.ESTADO_DISPONIBLE
                ),
                ProductoEntity(
                    codigo = "FLT-DENSO-CAB",
                    nombre = "Filtro de Polen Cabina Carbón Activado",
                    marca = "Denso",
                    categoria = "Filtros",
                    unidadMedida = "Unidad",
                    cantidad = 9,
                    stockMinimo = 4,
                    precioCompra = 7200.0,
                    precioVenta = 13490.0,
                    ubicacionZona = "B",
                    ubicacionEstante = "2",
                    ubicacionRepisa = "A",
                    proveedorNombre = "Repuestos Chile SpA",
                    estado = ProductoEntity.ESTADO_DISPONIBLE
                ),
                ProductoEntity(
                    codigo = "LIQ-BOSCH-DOT4",
                    nombre = "Líquido de Frenos DOT 4 Racing 500ml",
                    marca = "Bosch",
                    categoria = "Frenos",
                    unidadMedida = "Unidad",
                    cantidad = 0,
                    stockMinimo = 5,
                    precioCompra = 4200.0,
                    precioVenta = 7490.0,
                    ubicacionZona = "A",
                    ubicacionEstante = "3",
                    ubicacionRepisa = "C",
                    proveedorNombre = "Distribuidora Automotriz Sur",
                    estado = ProductoEntity.ESTADO_AGOTADO
                ),
                ProductoEntity(
                    codigo = "PAS-BREM-P85020",
                    nombre = "Pastillas de Freno Cerámicas Delanteras",
                    marca = "Brembo",
                    categoria = "Frenos",
                    unidadMedida = "Kit",
                    cantidad = 4,
                    stockMinimo = 4,
                    precioCompra = 34000.0,
                    precioVenta = 49990.0,
                    ubicacionZona = "A",
                    ubicacionEstante = "2",
                    ubicacionRepisa = "B",
                    proveedorNombre = "Frenos & Embragues Santiago",
                    estado = ProductoEntity.ESTADO_BAJO_STOCK
                ),
                ProductoEntity(
                    codigo = "DISC-FER-DDF1140",
                    nombre = "Discos de Freno Ventilados Delanteros (Par)",
                    marca = "Ferodo",
                    categoria = "Frenos",
                    unidadMedida = "Kit",
                    cantidad = 2,
                    stockMinimo = 3,
                    precioCompra = 48000.0,
                    precioVenta = 69990.0,
                    ubicacionZona = "A",
                    ubicacionEstante = "3",
                    ubicacionRepisa = "A",
                    proveedorNombre = "Frenos & Embragues Santiago",
                    estado = ProductoEntity.ESTADO_BAJO_STOCK
                ),
                ProductoEntity(
                    codigo = "BAT-BOSCH-S470",
                    nombre = "Batería Libre Mantención S4 70Ah 640A Borne (+)",
                    marca = "Bosch",
                    categoria = "Baterías",
                    unidadMedida = "Unidad",
                    cantidad = 5,
                    stockMinimo = 3,
                    precioCompra = 68000.0,
                    precioVenta = 94990.0,
                    ubicacionZona = "D",
                    ubicacionEstante = "1",
                    ubicacionRepisa = "A",
                    proveedorNombre = "Baterías Santiago Centro",
                    estado = ProductoEntity.ESTADO_DISPONIBLE
                ),
                ProductoEntity(
                    codigo = "BAT-HANKOOK-60",
                    nombre = "Batería MF56068 60Ah 520A Blindada",
                    marca = "Hankook",
                    categoria = "Baterías",
                    unidadMedida = "Unidad",
                    cantidad = 1,
                    stockMinimo = 4,
                    precioCompra = 52000.0,
                    precioVenta = 76990.0,
                    ubicacionZona = "D",
                    ubicacionEstante = "1",
                    ubicacionRepisa = "B",
                    proveedorNombre = "Baterías Santiago Centro",
                    estado = ProductoEntity.ESTADO_BAJO_STOCK
                ),
                ProductoEntity(
                    codigo = "BUJ-NGK-ILZKR",
                    nombre = "Bujía Iridio Laser ILZKR7B-11 (Pack 4)",
                    marca = "NGK",
                    categoria = "Encendido",
                    unidadMedida = "Juego",
                    cantidad = 10,
                    stockMinimo = 4,
                    precioCompra = 21500.0,
                    precioVenta = 32990.0,
                    ubicacionZona = "C",
                    ubicacionEstante = "1",
                    ubicacionRepisa = "A",
                    proveedorNombre = "Repuestos Puente Alto",
                    estado = ProductoEntity.ESTADO_DISPONIBLE
                ),
                ProductoEntity(
                    codigo = "BOB-BOSCH-0221",
                    nombre = "Bobina de Encendido Electrónica Directa",
                    marca = "Bosch",
                    categoria = "Encendido",
                    unidadMedida = "Unidad",
                    cantidad = 7,
                    stockMinimo = 4,
                    precioCompra = 26000.0,
                    precioVenta = 39990.0,
                    ubicacionZona = "C",
                    ubicacionEstante = "1",
                    ubicacionRepisa = "B",
                    proveedorNombre = "Repuestos Puente Alto",
                    estado = ProductoEntity.ESTADO_DISPONIBLE
                ),
                ProductoEntity(
                    codigo = "AMOR-MONR-7420",
                    nombre = "Amortiguador Delantero a Gas OESpectrum",
                    marca = "Monroe",
                    categoria = "Suspensión",
                    unidadMedida = "Unidad",
                    cantidad = 6,
                    stockMinimo = 4,
                    precioCompra = 42000.0,
                    precioVenta = 62990.0,
                    ubicacionZona = "C",
                    ubicacionEstante = "2",
                    ubicacionRepisa = "A",
                    proveedorNombre = "Suspensiones del Sur SpA",
                    estado = ProductoEntity.ESTADO_DISPONIBLE
                ),
                ProductoEntity(
                    codigo = "BIEL-MOOG-K750",
                    nombre = "Bieleta Barra Estabilizadora Reforzada",
                    marca = "Moog",
                    categoria = "Suspensión",
                    unidadMedida = "Unidad",
                    cantidad = 0,
                    stockMinimo = 6,
                    precioCompra = 9500.0,
                    precioVenta = 16990.0,
                    ubicacionZona = "C",
                    ubicacionEstante = "2",
                    ubicacionRepisa = "B",
                    proveedorNombre = "Suspensiones del Sur SpA",
                    estado = ProductoEntity.ESTADO_AGOTADO
                ),
                ProductoEntity(
                    codigo = "NEU-MICH-20555",
                    nombre = "Neumático Primacy 4 205/55 R16 91V",
                    marca = "Michelin",
                    categoria = "Neumáticos",
                    unidadMedida = "Unidad",
                    cantidad = 8,
                    stockMinimo = 4,
                    precioCompra = 69900.0,
                    precioVenta = 98990.0,
                    ubicacionZona = "E",
                    ubicacionEstante = "1",
                    ubicacionRepisa = "A",
                    proveedorNombre = "Distribuidora Neumáticos Chile",
                    estado = ProductoEntity.ESTADO_DISPONIBLE
                ),
                ProductoEntity(
                    codigo = "NEU-BRID-21560",
                    nombre = "Neumático Turanza T005 215/60 R17 96H",
                    marca = "Bridgestone",
                    categoria = "Neumáticos",
                    unidadMedida = "Unidad",
                    cantidad = 2,
                    stockMinimo = 4,
                    precioCompra = 84000.0,
                    precioVenta = 11990.0,
                    ubicacionZona = "E",
                    ubicacionEstante = "1",
                    ubicacionRepisa = "B",
                    proveedorNombre = "Distribuidora Neumáticos Chile",
                    estado = ProductoEntity.ESTADO_BAJO_STOCK
                ),
                ProductoEntity(
                    codigo = "KIT-DIST-GATES",
                    nombre = "Kit Distribución con Correa y Bomba de Agua",
                    marca = "Gates",
                    categoria = "Accesorios",
                    unidadMedida = "Kit",
                    cantidad = 3,
                    stockMinimo = 3,
                    precioCompra = 55000.0,
                    precioVenta = 79990.0,
                    ubicacionZona = "B",
                    ubicacionEstante = "3",
                    ubicacionRepisa = "A",
                    proveedorNombre = "Distribuidora Automotriz Sur",
                    estado = ProductoEntity.ESTADO_BAJO_STOCK
                )
            )
            dao.insertAll(productosIniciales)
        }
    }
}

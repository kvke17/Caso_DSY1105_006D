# Sistema de Gestión de Inventario Automotriz (Taller San Cristóbal - Puente Alto)

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android%2014%2B%20(API%2024--36)-green.svg?logo=android)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4.svg?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Room Database](https://img.shields.io/badge/Storage-Room%202.8.5-orange.svg?logo=sqlite)](https://developer.android.com/training/data-storage/room)
[![Spring Boot](https://img.shields.io/badge/Backend-Spring%20Boot%203.3.5-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![Retrofit](https://img.shields.io/badge/Network-Retrofit%202.11.0-blueviolet.svg)](https://square.github.io/retrofit/)

Proyecto desarrollado para la asignatura **DSY1105 - Desarrollo de Aplicaciones Móviles** en **Duoc UC**.  
Solución integral cliente-servidor para el control de inventario, telemetría de stock crítico, reposición de piezas, escaneo QR/barras y cotizaciones de importación en el **Taller Mecánico San Cristóbal**, ubicado en la comuna de **Puente Alto, Santiago de Chile**.

---

## 👥 1. Integrantes del Equipo

* **Jorge Araya**
* **César Adasme**
* **Asignatura:** DSY1105 - Desarrollo de Aplicaciones Móviles
* **Sección:** DSY1105-006D
* **Institución:** Duoc UC Puente Alto
* **Repositorio GitHub:** [Caso_DSY1105_006D](https://github.com/kvke17/Caso_DSY1105_006D)

---

## 📱 2. Contexto y Problemática

En talleres automotrices con alta afluencia vehicular, la gestión no sistematizada de repuestos e insumos produce quiebres imprevistos de stock, retrasos en entregas de vehículos y falta de visibilidad del capital inmovilizado. Asimismo, los repuestos importados dependen directamente de variaciones cambiarias en el **Dólar observado** y la **Unidad de Fomento (UF)**.

**La solución implementada entrega:**
* **Sincronización Híbrida en Tiempo Real**: Conexión con un microservicio Spring Boot como backend centralizado y persistencia en Room como respaldo local y caché transparente ante cortes de red.
* **Telemetría visual de stock y reposición automática**: Monitoreo neón de bajo stock y cálculo de la cantidad mínima sugerida: `maxOf(0, (Stock Mínimo × 2) - Cantidad)`.
* **Cotizaciones de referencia para repuestos importados**: Consumo de la API externa pública de `mindicador.cl` para mostrar el valor del día del Dólar y UF en la cabecera del catálogo.
* **Trazabilidad física en bodega**: Identificación visual de Zona, Estante y Repisa.
* **Escáner nativo CameraX**: Lectura instantánea de códigos QR y de barras para búsqueda y registro.

---

## 🏗️ 3. Arquitectura y Tecnologías

La aplicación implementa los principios de **Clean Architecture** bajo el patrón **MVVM (Model-View-ViewModel)** en Android junto con un microservicio REST en Spring Boot:

```
Caso_DSY1105_006D-main/
├── backend/                                   # Microservicio Spring Boot
│   ├── src/main/java/com/taller/inventario/backend/
│   │   ├── config/DataInitializer.java        # Precarga inicial en H2
│   │   ├── controller/ProductoController.java # Endpoints RESTful CRUD
│   │   ├── model/Producto.java                # Entidad JPA
│   │   ├── repository/ProductoRepository.java # JpaRepository
│   │   └── BackendApplication.java            # Entry point Spring Boot
│   ├── src/main/resources/application.properties # Puerto 8080 y H2 file DB
│   ├── data/inventario_db.mv.db               # Persistencia real en disco
│   ├── pom.xml                                # Dependencias Maven
│   └── mvnw / mvnw.cmd                        # Maven Wrapper independiente
│
└── app/src/main/java/com/taller/inventario/   # Aplicación Android Jetpack Compose
    ├── data/
    │   ├── local/                             # Persistencia Local (Room)
    │   │   ├── AppDatabase.kt                 # Room Database con respaldo offline
    │   │   ├── ProductoDao.kt                 # DAO reactivo con Flow
    │   │   └── ProductoEntity.kt              # Entidad con lógica de negocio
    │   ├── remote/                            # Capa de Red (Retrofit)
    │   │   ├── InventarioApiService.kt        # Cliente REST para Spring Boot (10.0.2.2:8080)
    │   │   └── ExternalApiService.kt          # Cliente REST para mindicador.cl (UF / Dólar)
    │   └── repository/
    │       └── ProductoRepository.kt          # Single Source of Truth híbrido
    ├── ui/
    │   ├── navigation/AppNavigation.kt        # Grafo de navegación Compose
    │   ├── producto/                          # Vistas y ViewModel
    │   │   ├── ListaProductosScreen.kt        # Catálogo con dashboard e indicadores
    │   │   ├── ProductoFormScreen.kt          # Formulario con validaciones estrictas
    │   │   ├── ProductoUiState.kt             # Estado inmutable de UI
    │   │   └── ProductoViewModel.kt           # Orquestador reactivo MVVM
    │   ├── qr/QrScannerScreen.kt              # Visor nativo con CameraX y ML Kit
    │   └── theme/                             # Sistema de diseño Cyberpunk Neón
    └── utils/                                 # Helpers nativos (permisos, vibración, llamadas)
```

### Tecnologías Clave
* **Cliente Móvil**: Kotlin 2.2.10, Jetpack Compose Material 3, Android SDK 36 (minSdk 24), Room Database 2.8.5, Retrofit 2.11.0, Gson, OkHttp Logging Interceptor, CameraX 1.4.2, Google ML Kit Barcode Scanning.
* **Microservicio Backend**: Java 17/21, Spring Boot 3.3.5, Spring Data JPA, Spring Web MVC, Base de datos H2 en modo archivo persistente (`./backend/data/inventario_db`), Maven Wrapper.
* **API Externa**: `https://mindicador.cl/api/` (datos macroeconómicos oficiales de Chile).
* **Pruebas Unitarias**: JUnit 4, MockK 1.13.13, Kotlinx Coroutines Test 1.9.0.

---

## 📡 4. Tabla de Endpoints y Ejemplos de Petición

### A. Endpoints del Microservicio Spring Boot (`http://localhost:8080` / `http://10.0.2.2:8080`)

| Método | Endpoint | Descripción | Código Éxito |
|---|---|---|---|
| `GET` | `/api/productos` | Listar todos los repuestos del taller | `200 OK` |
| `GET` | `/api/productos/{id}` | Obtener detalle de un repuesto por su código | `200 OK` / `404 Not Found` |
| `POST` | `/api/productos` | Registrar nuevo repuesto | `201 Created` / `400 Bad Request` |
| `PUT` | `/api/productos/{id}` | Actualizar datos de un repuesto existente | `200 OK` |
| `DELETE` | `/api/productos/{id}` | Eliminar repuesto por su código | `204 No Content` / `404 Not Found` |

#### Ejemplo JSON de Repuesto (`GET / POST / PUT`):
```json
{
  "codigo": "ACE-CAST-5W30",
  "nombre": "Aceite Sintético Edge 5W-30 4L",
  "marca": "Castrol",
  "categoria": "Lubricantes",
  "unidadMedida": "Bidón",
  "cantidad": 16,
  "stockMinimo": 6,
  "precioCompra": 28000.0,
  "precioVenta": 39990.0,
  "ubicacionZona": "A",
  "ubicacionEstante": "1",
  "ubicacionRepisa": "A",
  "proveedorNombre": "Castrol Chile",
  "estado": "DISPONIBLE"
}
```

#### Consola Web H2 Database:
* **URL:** `http://localhost:8080/h2-console`
* **JDBC URL:** `jdbc:h2:file:./data/inventario_db`
* **Usuario:** `sa`
* **Contraseña:** *(vacía)*

---

### B. Endpoint de la API Externa Independiente (`https://mindicador.cl/api/`)

| Método | Endpoint | Descripción | Datos Consumidos |
|---|---|---|---|
| `GET` | `https://mindicador.cl/api` | Consulta en tiempo real de indicadores de Chile | Dólar Observado (`dolar.valor`) y Unidad de Fomento (`uf.valor`) |

#### Ejemplo de Respuesta JSON de `mindicador.cl`:
```json
{
  "version": "1.7.0",
  "autor": "mindicador.cl",
  "fecha": "2026-11-20T11:00:00.000Z",
  "uf": {
    "codigo": "uf",
    "nombre": "Unidad de fomento (UF)",
    "unidad_medida": "Pesos",
    "fecha": "2026-11-20T03:00:00.000Z",
    "valor": 41371.05
  },
  "dolar": {
    "codigo": "dolar",
    "nombre": "Dólar observado",
    "unidad_medida": "Pesos",
    "fecha": "2026-11-20T03:00:00.000Z",
    "valor": 1008.79
  }
}
```

---

## 💻 5. Comandos de Ejecución y Compilación

### Paso 1: Levantar el Microservicio Backend (Spring Boot)
Abrir una terminal en el directorio `./backend/` y ejecutar:

* **En Windows (PowerShell / CMD):**
  ```powershell
  cd backend
  .\mvnw.cmd spring-boot:run
  ```

* **En Linux / macOS:**
  ```bash
  cd backend
  ./mvnw spring-boot:run
  ```

El servicio iniciará en el puerto `8080` y creará automáticamente la base de datos persistente en `./backend/data/inventario_db.mv.db`.

---

### Paso 2: Ejecutar Pruebas Unitarias de Android (100% Cobertura Lógica)
Desde la carpeta raíz del proyecto:

* **En Windows:**
  ```powershell
  .\gradlew.bat testDebugUnitTest
  ```

* **En Linux / macOS:**
  ```bash
  ./gradlew testDebugUnitTest
  ```

Las 34 pruebas unitarias (de `ProductoEntityTest`, `ProductoRepositoryTest` y `ProductoViewModelTest`) se ejecutan y validan en verde.

---

### Paso 3: Compilar y Generar el APK de la Aplicación Móvil

* **Generar APK Debug:**
  ```bash
  ./gradlew assembleDebug
  ```
  El instalador se generará en:  
  `app/build/outputs/apk/debug/app-debug.apk`

* **Generar APK Release:**
  ```bash
  ./gradlew assembleRelease
  ```
  El instalador se generará en:  
  `app/build/outputs/apk/release/app-release-unsigned.apk` (o firmado con Keystore).

---

## 🧪 6. Resumen de Pruebas Unitarias Implementadas

Se implementaron suites exhaustivas con **MockK** y **Coroutines Test**:
1. **`ProductoEntityTest`**: Reglas puras de negocio (cálculo de reposición automática `(stockMinimo * 2) - cantidad`, estados `DISPONIBLE`, `BAJO_STOCK`, `AGOTADO` y formato de ubicación física).
2. **`ProductoRepositoryTest`**:
   - Operaciones CRUD híbridas (Room local + Spring Boot remoto).
   - Respaldo automático en Room ante fallos de conectividad o apagado del backend.
   - Sincronización remota (`sincronizarDesdeRemoto()`).
   - Detección de repuestos con stock crítico.
   - Consumo y deserialización de la API externa `mindicador.cl` con control de excepciones.
3. **`ProductoViewModelTest`**:
   - Flujo reactivo StateFlow con combinación de filtros y búsqueda.
   - Transiciones automáticas de stock al ingresar cantidades.
   - Validación estricta de formularios (campos vacíos, cantidades positivas, márgenes de ganancia venta >= costo).
   - Rechazo de códigos duplicados en creación.
   - Carga reactiva de cotizaciones Dólar/UF.
   - Flujo de confirmación y eliminación de productos.

---

## 🔏 ## Evidencia de APK Firmado y Keystore (.jks)
<!-- Sección reservada para adjuntar captura de pantalla del APK firmado y archivo keystore (.jks) -->

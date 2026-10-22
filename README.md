# Sistema de Gestión de Inventario Automotriz (Taller San Cristóbal - Puente Alto)

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android%2014%2B%20(API%2024--36)-green.svg?logo=android)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4.svg?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Room Database](https://img.shields.io/badge/Storage-Room%202.8.5-orange.svg?logo=sqlite)](https://developer.android.com/training/data-storage/room)

Proyecto desarrollado para la asignatura **DSY1105 - Desarrollo de Aplicaciones Móviles** en **Duoc UC**.  
Solución móvil integral para el control de stock, reposición automática, catálogo técnico y escaneo QR de repuestos en el **Taller Mecánico San Cristóbal** ubicado en la comuna de **Puente Alto, Santiago de Chile**.

---

## 👥 1. Integrantes del Equipo

* **Estudiante 1:** [Nombre y Apellido del Estudiante 1]
* **Estudiante 2:** [Nombre y Apellido del Estudiante 2]
* **Asignatura:** DSY1105 - Desarrollo de Aplicaciones Móviles
* **Sección:** [Ej. DSY1105-001D]
* **Sede:** Duoc UC Puente Alto
* **Docente:** [Nombre del Docente]

---

## 📱 2. Descripción de la Problemática y Solución

En talleres mecánicos de alta rotación, el control manual o en cuadernos de los repuestos ocasiona quiebres de stock, pérdidas económicas y retrasos en las reparaciones de vehículos de clientes.

**Esta aplicación resuelve este problema entregando:**
* **Catálogo de repuestos en tiempo real**: Registro de lubricantes, filtros, frenos, baterías, bujías, suspensión y accesorios.
* **Telemetría visual de stock**: Barra dinámica con resplandor neón que alerta visualmente cuando un repuesto está próximo a agotarse.
* **Fórmula de reposición automática**: Sugiere la cantidad exacta a comprar según la regla de seguridad del taller: `maxOf(0, (Stock Mínimo × 2) - Cantidad)`.
* **Ubicación física en bodega**: Identificación rápida de Zona, Estante y Repisa para el mecánico.
* **Valorización económica en pesos chilenos (CLP)**: Monitoreo del capital total invertido en repuestos y cálculo automático del margen de ganancia.

---

## 🚀 3. Funcionalidades Implementadas

### A. Catálogo y Dashboard Gerencial
- **2 Tarjetas Maestras de Telemetría**:
  1. *En Bodega*: Total de repuestos y valorización económica del inventario en CLP.
  2. *Stock Crítico*: Contador interactivo que al pulsar filtra en un toque los repuestos en alerta de agotamiento.
- **Buscador Reactivo**: Filtrado instantáneo por nombre, código de parte, marca o proveedor.
- **Filtros por Categoría**: Selector en pastillas con contadores y colores neón identificadores.

### B. Formularios Validados y Desacoplados (MVVM)
- Validación de campos requeridos (código único, nombre, marca, categoría, cantidades y precios).
- Estimador de margen comercial en vivo mientras el usuario digita costo y venta.
- Limpieza automática de errores en pantalla según el patrón StateFlow.

### C. Animaciones Visuales Fluidas (Rúbrica IE 2.2.2)
- **Barra de nivel de stock animada**: Transición fluida con `animateFloatAsState` al cargar o filtrar la lista.
- **Alertas de reposición animadas**: Entrada suave con `AnimatedVisibility(fadeIn() + expandVertically())`.

---

## 📲 4. Acceso a Recursos Nativos del Dispositivo (Rúbrica IE 2.4.1)

La aplicación integra de forma segura **3 recursos nativos del hardware**:

1. **Cámara Nativa (CameraX + Google ML Kit Barcode)**:
   - Escaneo en tiempo real de códigos de barra y códigos QR de repuestos con previsualización asistida.
   - Solicitud segura de permisos en tiempo de ejecución (`android.permission.CAMERA`).
2. **Telefonía y Marcador Nativo (`Intent.ACTION_DIAL`)**:
   - Botón directo en cada tarjeta de repuesto para llamar inmediatamente al proveedor asignado vía el marcador nativo del dispositivo.
3. **Motor Háptico de Vibración Física (`Vibrator API`)**:
   - Retroalimentación táctil nativa al escanear un código QR con éxito, al confirmar guardado o al eliminar un repuesto (`android.permission.VIBRATE`).

---

## 🏗️ 5. Arquitectura y Tecnologías

El proyecto sigue rigurosamente los principios de **Clean Architecture** y directrices oficiales de Google:

```
app/src/main/java/com/taller/inventario/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt          # Room Database Singleton con precarga de 18 repuestos
│   │   ├── ProductoDao.kt          # Consultas reactivas (Flow, Insert, Delete, Count)
│   │   └── ProductoEntity.kt       # Entidad Room con reglas de negocio puras
│   ├── model/
│   │   └── QrResult.kt             # Modelo de lectura QR
│   └── repository/
│       ├── ProductoRepository.kt   # Única fuente de verdad (Single Source of Truth)
│       └── QrRepository.kt         # Lógica de decodificación QR
├── ui/
│   ├── navigation/
│   │   ├── AppNavigation.kt        # NavHost y enrutamiento Compose
│   │   └── Screen.kt               # Contrato sellado de rutas seguras
│   ├── producto/
│   │   ├── components/
│   │   │   ├── ProductCard.kt      # Tarjeta con telemetría de stock y botones nativos
│   │   │   └── StockBadge.kt       # Insignia accesible (Disponible, Bajo Stock, Agotado)
│   │   ├── ListaProductosScreen.kt # Pantalla principal con dashboard de KPIs y filtros
│   │   ├── ProductoFormScreen.kt   # Formulario reactivo con cálculo de márgenes
│   │   ├── ProductoUiState.kt      # Estado inmutable de la interfaz
│   │   └── ProductoViewModel.kt    # Lógica de negocio, validaciones y StateFlow reactivo
│   ├── qr/
│   │   ├── QrScannerScreen.kt      # Pantalla con visor de cámara nativa CameraX
│   │   └── QrViewModel.kt          # ViewModel para gestión del escaneo QR
│   └── theme/
│       ├── Color.kt                # Paleta Dark Neon Automotriz de alto contraste
│       ├── Theme.kt                # MaterialTheme M3 configurado en modo oscuro
│       └── Type.kt                 # Escala tipográfica optimizada para lectura en taller
└── utils/
    ├── CameraPermissionHelper.kt   # Verificación centralizada de permisos
    ├── NativeDeviceHelper.kt       # Llamadas telefónicas nativas y vibrador háptico
    └── QrScanner.kt                # Implementación nativa CameraX + ML Kit
```

---

## ⚙️ 6. Requisitos y Pasos de Ejecución

### Requisitos Previos
* **Android Studio**: Ladybug (2024.2+) o Meerkat / Jellyfish.
* **JDK**: OpenJDK 17 o 21 (incluido en Android Studio).
* **Dispositivo / Emulador**: Android 7.0 (API 24) o superior.

### Pasos para Ejecutar
1. Clonar el repositorio en tu computador:
   ```bash
   git clone <URL_DEL_REPOSITORIO_GITHUB>
   ```
2. Abrir **Android Studio**.
3. Seleccionar **File ➔ Open** y elegir la carpeta del proyecto.
4. Esperar que **Gradle Sync** descargue las dependencias.
5. Iniciar un emulador Android (ej. *Medium Phone API 34 o superior*) o conectar un teléfono físico con depuración USB activada.
6. Presionar el botón verde de **Run** (**▶️** o `Shift + F10`).

### Generación del archivo APK
Para generar el instalador en cualquier momento desde terminal:
```bash
./gradlew assembleDebug
```
El archivo generado se ubicará en:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📋 7. Planificación y Gestión en Trello

El desarrollo del proyecto se planificó colaborativamente siguiendo metodología ágil:

* **Tablero Trello del Proyecto:** [Enlace a tu tablero público de Trello]
* **Columnas de Seguimiento:**
  * 📌 *Backlog de Requerimientos*: Historias de usuario del taller automotriz.
  * ⚙️ *En Progreso (Sprints)*: Implementación de Room, Compose UI, CameraX y Native Calls.
  * 🧪 *En Pruebas / QA*: Validación de pruebas unitarias y feedback de stock.
  * ✅ *Terminado / Entregado*: Versión final lista para Evaluación Parcial 2.

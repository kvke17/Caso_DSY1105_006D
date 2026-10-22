package com.taller.inventario.ui.navigation

/**
 * Definición centralizada y segura de rutas de navegación de la aplicación.
 */
sealed class Screen(val route: String) {
    data object ListaProductos : Screen("lista_productos")
    
    data object ProductoForm : Screen("producto_form?codigo={codigo}") {
        fun createRoute(codigo: String? = null): String {
            return if (codigo.isNullOrBlank()) {
                "producto_form"
            } else {
                "producto_form?codigo=$codigo"
            }
        }
    }

    data object QrScanner : Screen("qr_scanner?target={target}") {
        fun createRoute(target: String = "form"): String = "qr_scanner?target=$target"
    }
}

package com.taller.inventario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.taller.inventario.ui.navigation.AppNavigation
import com.taller.inventario.ui.producto.ProductoViewModel
import com.taller.inventario.ui.producto.ProductoViewModelFactory
import com.taller.inventario.ui.theme.InventarioTheme

/**
 * Actividad principal de la aplicación.
 * Configura Compose, el tema visual de taller mecánico y el grafo de navegación.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: ProductoViewModel by viewModels {
        val app = application as InventarioApp
        ProductoViewModelFactory(app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InventarioTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}

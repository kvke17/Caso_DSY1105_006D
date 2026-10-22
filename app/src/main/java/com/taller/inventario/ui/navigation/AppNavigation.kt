package com.taller.inventario.ui.navigation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taller.inventario.ui.producto.ListaProductosScreen
import com.taller.inventario.ui.producto.ProductoFormScreen
import com.taller.inventario.ui.producto.ProductoViewModel
import com.taller.inventario.ui.qr.QrScannerScreen
import com.taller.inventario.ui.qr.QrViewModel
import com.taller.inventario.utils.CameraPermissionHelper

/**
 * Grafo central de navegación para la aplicación de inventario.
 * Conecta el listado, el formulario y la pantalla pedagógica de escaneo QR.
 */
@Composable
fun AppNavigation(
    viewModel: ProductoViewModel,
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(CameraPermissionHelper.hasCameraPermission(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    NavHost(
        navController = navController,
        startDestination = Screen.ListaProductos.route
    ) {
        composable(route = Screen.ListaProductos.route) {
            ListaProductosScreen(
                uiState = uiState,
                viewModel = viewModel,
                onNavigateToCreate = {
                    navController.navigate(Screen.ProductoForm.createRoute())
                },
                onNavigateToEdit = { codigo ->
                    navController.navigate(Screen.ProductoForm.createRoute(codigo))
                },
                onNavigateToScanner = {
                    navController.navigate(Screen.QrScanner.createRoute("search"))
                }
            )
        }

        composable(
            route = Screen.ProductoForm.route,
            arguments = listOf(
                navArgument("codigo") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val codigo = backStackEntry.arguments?.getString("codigo")
            ProductoFormScreen(
                codigoParaEditar = codigo,
                uiState = uiState,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToScanner = {
                    navController.navigate(Screen.QrScanner.createRoute("form"))
                }
            )
        }

        composable(
            route = Screen.QrScanner.route,
            arguments = listOf(
                navArgument("target") {
                    type = NavType.StringType
                    defaultValue = "form"
                }
            )
        ) { backStackEntry ->
            val target = backStackEntry.arguments?.getString("target") ?: "form"
            val qrViewModel: QrViewModel = viewModel()

            QrScannerScreen(
                viewModel = qrViewModel,
                hasCameraPermission = hasCameraPermission,
                onRequestPermission = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onCodeSelected = { code ->
                    if (target == "search") {
                        viewModel.onSearchQueryChange(code)
                    } else {
                        viewModel.onCodigoChange(code)
                    }
                    qrViewModel.clearResult()
                    navController.popBackStack()
                },
                onNavigateBack = {
                    qrViewModel.clearResult()
                    navController.popBackStack()
                }
            )
        }
    }
}

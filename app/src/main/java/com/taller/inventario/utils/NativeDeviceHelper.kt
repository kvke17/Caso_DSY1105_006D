package com.taller.inventario.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast

/**
 * Gestor centralizado de acceso a recursos nativos del dispositivo móvil.
 * Cumple con el Indicador de Logro IE 2.4.1 de la asignatura DSY1105 - Duoc UC:
 * - Recurso 1: Cámara de hardware (CameraX y ML Kit Barcode en QrScanner.kt)
 * - Recurso 2: Telefonía y marcador nativo (Intent.ACTION_DIAL)
 * - Recurso 3: Motor de vibración física háptica (Vibrator Hardware API)
 */
object NativeDeviceHelper {

    /**
     * Acceso al recurso nativo de telefonía mediante el marcador oficial de Android.
     * Permite al mecánico o bodeguero llamar inmediatamente al proveedor asignado al repuesto.
     */
    fun dialPhoneNumber(context: Context, phoneNumber: String) {
        try {
            val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$cleanNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir el marcador telefónico", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Acceso al hardware nativo de vibración para retroalimentación táctil inmediata
     * al escanear códigos QR, confirmar guardados o eliminar productos.
     */
    fun vibrate(context: Context, durationMs: Long = 80L) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vibrator?.let {
                if (it.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        it.vibrate(durationMs)
                    }
                }
            }
        } catch (_: Exception) {
            // Silencioso si el dispositivo no soporta vibración
        }
    }
}

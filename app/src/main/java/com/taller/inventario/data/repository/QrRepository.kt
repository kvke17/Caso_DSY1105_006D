package com.taller.inventario.data.repository

import com.taller.inventario.data.model.QrResult

class QrRepository {
    fun processQrContent(content: String): QrResult {
        return QrResult(content)
    }
}

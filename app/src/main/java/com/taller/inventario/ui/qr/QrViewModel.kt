package com.taller.inventario.ui.qr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taller.inventario.data.model.QrResult
import com.taller.inventario.data.repository.QrRepository

class QrViewModel : ViewModel() {
    private val repository = QrRepository()

    private val _qrResult = MutableLiveData<QrResult?>()
    val qrResult: LiveData<QrResult?> = _qrResult

    fun onQrDetected(content: String) {
        _qrResult.value = repository.processQrContent(content)
    }

    fun clearResult() {
        _qrResult.value = null
    }
}

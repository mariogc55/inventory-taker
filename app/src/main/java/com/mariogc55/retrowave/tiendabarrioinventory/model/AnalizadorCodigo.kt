package com.mariogc55.retrowave.tiendabarrioinventory.model

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class AnalizadorCodigo(
    private val onCodigoDetectado: (CodigoEscaneado) -> Unit
) : ImageAnalysis.Analyzer {

    private val escaner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_QR_CODE)
            .build()
    )

    @Volatile
    private var detectado = false

    @Volatile
    private var inicioIntento = System.currentTimeMillis()

    fun reiniciar() {
        detectado = false
        inicioIntento = System.currentTimeMillis()
    }

    fun cerrar() {
        escaner.close()
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val imagenCamara = imageProxy.image
        if (imagenCamara == null || detectado) {
            imageProxy.close()
            return
        }

        val imagen = InputImage.fromMediaImage(imagenCamara, imageProxy.imageInfo.rotationDegrees)

        escaner.process(imagen)
            .addOnSuccessListener { codigos ->
                val codigo = codigos.firstOrNull { !it.rawValue.isNullOrBlank() }
                if (codigo != null && !detectado) {
                    detectado = true
                    val tipo = if (codigo.format == Barcode.FORMAT_EAN_13) TipoCodigo.EAN_13 else TipoCodigo.QR
                    onCodigoDetectado(
                        CodigoEscaneado(
                            valor = codigo.rawValue!!,
                            tipo = tipo,
                            tiempoDeteccionMs = System.currentTimeMillis() - inicioIntento
                        )
                    )
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}
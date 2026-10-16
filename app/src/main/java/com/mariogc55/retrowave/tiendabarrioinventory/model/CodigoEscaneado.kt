package com.mariogc55.retrowave.tiendabarrioinventory.model

enum class TipoCodigo(val etiqueta: String) {
    EAN_13("EAN-13"),
    QR("QR")
}

data class CodigoEscaneado(
    val valor: String,
    val tipo: TipoCodigo,
    val tiempoDeteccionMs: Long
) {
    val cumpleTiempoMaximo: Boolean
        get() = tiempoDeteccionMs <= TIEMPO_MAXIMO_DETECCION_MS

    val tiempoEnSegundos: String
        get() = "%.1f s".format(tiempoDeteccionMs / 1000.0)

    companion object {
        const val TIEMPO_MAXIMO_DETECCION_MS = 2000L
        const val TIEMPO_REINTENTO_MS = 6000L
    }
}
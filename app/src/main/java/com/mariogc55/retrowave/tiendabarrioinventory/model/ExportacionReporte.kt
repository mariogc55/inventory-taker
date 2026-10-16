package com.mariogc55.retrowave.tiendabarrioinventory.model

enum class FormatoExportacion(val extension: String, val mimeType: String) {
    EXCEL("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
}

data class SolicitudExportacion(
    val formato: FormatoExportacion,
    val rangoFechas: Pair<Int, Int>,
    val nombreArchivo: String
)

sealed class ResultadoExportacion {
    data class Exito(val nombreArchivo: String, val mesesExportados: Int) : ResultadoExportacion()
    data class Error(val mensaje: String) : ResultadoExportacion()
}
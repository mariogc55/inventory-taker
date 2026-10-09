package com.mariogc55.retrowave.tiendabarrioinventory.model

data class ComparacionMensual(
    val mesActual: String,
    val mesAnterior: String,
    val valorActual: Double,
    val valorAnterior: Double,
    val diferencia: Double,
    val porcentajeCambio: Double?,
    val etiqueta: String
)
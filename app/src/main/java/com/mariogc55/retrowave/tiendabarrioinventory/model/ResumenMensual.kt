package com.mariogc55.retrowave.tiendabarrioinventory.model

data class ResumenMensual(
    val mes: String,
    val totalVentas: Int,
    val totalIngresos: Double,
    val ganancia: Double
)
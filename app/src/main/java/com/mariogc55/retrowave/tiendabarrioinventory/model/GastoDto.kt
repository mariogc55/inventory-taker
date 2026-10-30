package com.mariogc55.retrowave.tiendabarrioinventory.model

import kotlinx.serialization.Serializable

@Serializable
data class GastoDto(
    val id: Long? = null,
    val descripcion: String,
    val monto: Double,
    val categoria: String
) {
    fun toGasto(): Gasto = Gasto(
        id = (id ?: 0L).toInt(),
        descripcion = descripcion,
        monto = monto,
        categoria = categoria
    )
}
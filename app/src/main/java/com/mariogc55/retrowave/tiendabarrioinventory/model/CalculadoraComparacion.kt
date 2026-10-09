package com.mariogc55.retrowave.tiendabarrioinventory.model

object CalculadoraComparacion {

    fun calcular(
        mesActual: String,
        mesAnterior: String,
        valorActual: Double,
        valorAnterior: Double,
        etiqueta: String
    ): ComparacionMensual {
        val diferencia = valorActual - valorAnterior

        val porcentajeCambio: Double? = if (valorAnterior == 0.0) {
            null
        } else {
            ((valorActual - valorAnterior) / valorAnterior) * 100
        }

        return ComparacionMensual(
            mesActual = mesActual,
            mesAnterior = mesAnterior,
            valorActual = valorActual,
            valorAnterior = valorAnterior,
            diferencia = diferencia,
            porcentajeCambio = porcentajeCambio,
            etiqueta = etiqueta
        )
    }

    fun hayDatosSuficientes(valorActual: Double, valorAnterior: Double): Boolean {
        return !(valorActual == 0.0 && valorAnterior == 0.0)
    }
}
package com.mariogc55.retrowave.tiendabarrioinventory.model

/**
 * RF-11: Lógica de cálculo para la comparación mes actual vs mes anterior.
 *
 * Reglas:
 * - diferencia = valor_actual − valor_anterior
 * - porcentaje_cambio = ((actual − anterior) / anterior) × 100
 * - Si anterior = 0, porcentaje_cambio = null (la UI muestra "N/A")
 * - Si ambos son 0, se marca como "Datos insuficientes para comparar"
 */
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
            null  // Caso N/A: no se puede dividir por cero
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

    /**
     * Verifica si hay datos suficientes para comparar.
     * Si ambos valores son 0, no tiene sentido mostrar comparación.
     */
    fun hayDatosSuficientes(valorActual: Double, valorAnterior: Double): Boolean {
        return !(valorActual == 0.0 && valorAnterior == 0.0)
    }
}
package com.mariogc55.retrowave.tiendabarrioinventory.model

/**
 * RF-11: Comparación mes actual vs mes anterior.
 *
 * Datos (según documento de requisitos):
 * - mes_actual: identificador del mes actual (formato "yyyy-MM")
 * - mes_anterior: identificador del mes anterior (formato "yyyy-MM")
 * - diferencia: valor actual − valor anterior
 * - porcentaje_cambio: ((actual − anterior) / anterior) × 100
 *
 * Si mes_anterior = 0, porcentaje_cambio debe mostrarse como "N/A" en la UI.
 */
data class ComparacionMensual(
    val mesActual: String,
    val mesAnterior: String,
    val valorActual: Double,
    val valorAnterior: Double,
    val diferencia: Double,
    val porcentajeCambio: Double?,  // null cuando mesAnterior = 0 → UI muestra "N/A"
    val etiqueta: String             // "Ingresos", "Ganancia bruta", etc.
)
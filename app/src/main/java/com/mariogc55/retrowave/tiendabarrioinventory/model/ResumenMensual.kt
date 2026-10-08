package com.mariogc55.retrowave.tiendabarrioinventory.model

/**
 * RF-12: Resumen histórico mensual.
 *
 * Datos (según documento de requisitos):
 * - mes: identificador del mes (formato "yyyy-MM" o nombre corto "Oct 2026")
 * - totalVentas: cantidad de ventas registradas en ese mes
 * - totalIngresos: suma de ingresos del mes
 * - ganancia: ganancia neta del mes (ingresos − gastos)
 *
 * Reglas:
 * - Se agrupan por mes calendario
 * - Se ordenan de más reciente a más antiguo
 * - Solo se muestran meses con al menos 1 transacción
 */
data class ResumenMensual(
    val mes: String,
    val totalVentas: Int,
    val totalIngresos: Double,
    val ganancia: Double
)
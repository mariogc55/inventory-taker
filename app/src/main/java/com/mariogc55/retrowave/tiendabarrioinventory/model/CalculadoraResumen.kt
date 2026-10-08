package com.mariogc55.retrowave.tiendabarrioinventory.model

/**
 * RF-12: Lógica de cálculo para el resumen histórico mensual.
 *
 * Reglas:
 * - Agrupar las transacciones por mes calendario
 * - Ordenar los meses de más reciente a más antiguo
 * - Solo incluir meses con al menos 1 transacción
 * - Calcular totales históricos (ingresos, gastos, ventas, ganancia neta)
 */
object CalculadoraResumen {

    /**
     * Filtra y ordena una lista de resúmenes mensuales según las reglas del RF-12:
     * - Solo meses con al menos 1 venta
     * - Ordenados de más reciente a más antiguo (asumiendo que la lista entra en orden)
     */
    fun filtrarYOrdenar(resumenes: List<ResumenMensual>): List<ResumenMensual> {
        return resumenes
            .filter { it.totalVentas >= 1 }
            .reversed()  // Más reciente primero (asumiendo que entra cronológico)
    }

    /**
     * Suma los ingresos totales históricos de todos los meses con transacciones.
     */
    fun calcularIngresosTotales(resumenes: List<ResumenMensual>): Double {
        return resumenes.sumOf { it.totalIngresos }
    }

    /**
     * Suma la ganancia neta histórica (puede ser negativa si hay más gastos que ingresos).
     */
    fun calcularGananciaTotal(resumenes: List<ResumenMensual>): Double {
        return resumenes.sumOf { it.ganancia }
    }

    /**
     * Suma el total de ventas (cantidad de transacciones) en todo el historial.
     */
    fun calcularTotalVentas(resumenes: List<ResumenMensual>): Int {
        return resumenes.sumOf { it.totalVentas }
    }

    /**
     * Calcula los gastos totales a partir de los ingresos y la ganancia:
     * gastos = ingresos − ganancia
     */
    fun calcularGastosTotales(resumenes: List<ResumenMensual>): Double {
        val ingresos = calcularIngresosTotales(resumenes)
        val ganancia = calcularGananciaTotal(resumenes)
        return ingresos - ganancia
    }

    /**
     * Verifica si hay transacciones registradas en el historial.
     * Si retorna false, la UI debe mostrar: "No hay transacciones registradas aún"
     */
    fun hayTransacciones(resumenes: List<ResumenMensual>): Boolean {
        return resumenes.any { it.totalVentas >= 1 }
    }
}
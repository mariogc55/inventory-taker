package com.mariogc55.retrowave.tiendabarrioinventory.model

object CalculadoraResumen {

    fun filtrarYOrdenar(resumenes: List<ResumenMensual>): List<ResumenMensual> {
        return resumenes
            .filter { it.totalVentas >= 1 }
            .reversed()
    }

    fun calcularIngresosTotales(resumenes: List<ResumenMensual>): Double {
        return resumenes.sumOf { it.totalIngresos }
    }

    fun calcularGananciaTotal(resumenes: List<ResumenMensual>): Double {
        return resumenes.sumOf { it.ganancia }
    }

    fun calcularTotalVentas(resumenes: List<ResumenMensual>): Int {
        return resumenes.sumOf { it.totalVentas }
    }

    fun calcularGastosTotales(resumenes: List<ResumenMensual>): Double {
        val ingresos = calcularIngresosTotales(resumenes)
        val ganancia = calcularGananciaTotal(resumenes)
        return ingresos - ganancia
    }

    fun hayTransacciones(resumenes: List<ResumenMensual>): Boolean {
        return resumenes.any { it.totalVentas >= 1 }
    }
}
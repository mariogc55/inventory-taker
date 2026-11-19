package com.mariogc55.retrowave.tiendabarrioinventory.model

import android.content.Context

data class ResumenGanancias(
    val ingresos: Double,
    val gastos: Double,
    val costoInventario: Double,
    val gananciaNeta: Double,
    val margenBruto: Double
)

class CalculadoraGanancias(context: Context) {

    private val productoRepository = ProductoRepository(context)

    suspend fun calcular(): ResumenGanancias {
        val productos = try {
            productoRepository.obtenerProductos()
        } catch (_: Exception) {
            emptyList()
        }

        val gastosList = try {
            GastoRepository.obtenerGastos()
        } catch (_: Exception) {
            emptyList()
        }

        val ingresos = productos.sumOf { it.sellingPrice * it.stock }
        val costoInventario = productos.sumOf { it.purchasePrice * it.stock }
        val gastos = gastosList.sumOf { it.monto }

        val gananciaNeta = ingresos - gastos

        val margenBruto = if (ingresos > 0.0) {
            ((ingresos - costoInventario) / ingresos) * 100
        } else {
            0.0
        }

        return ResumenGanancias(
            ingresos = ingresos,
            gastos = gastos,
            costoInventario = costoInventario,
            gananciaNeta = gananciaNeta,
            margenBruto = margenBruto
        )
    }
}
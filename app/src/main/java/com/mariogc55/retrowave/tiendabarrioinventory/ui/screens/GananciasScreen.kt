package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mariogc55.retrowave.tiendabarrioinventory.model.CalculadoraComparacion
import com.mariogc55.retrowave.tiendabarrioinventory.model.ComparacionMensual

@Composable
fun GananciasScreen() {
    // ===== Valores de ejemplo del mes actual (RF-10) =====
    // En una app real vendrían de las ventas y gastos registrados
    val ingresosMes = 3500000.0      // Suma de todas las ventas del mes
    val gastosMes = 1545000.0        // Suma de todos los gastos del mes (coincide con RF-09)
    val costoVentas = 1800000.0      // Suma del precio de compra de los productos vendidos

    // RF-10: ganancia_neta = ingresos − gastos
    val gananciaNeta = ingresosMes - gastosMes

    // RF-10: margen_bruto = ((ingresos − costo_ventas) / ingresos) × 100
    val margenBruto = if (ingresosMes > 0) {
        ((ingresosMes - costoVentas) / ingresosMes) * 100
    } else {
        0.0
    }

    // ===== RF-11: Datos del mes anterior para comparación =====
    val ingresosMesAnterior = 2400000.0
    val gastosMesAnterior = 1200000.0
    val gananciaNetaMesAnterior = ingresosMesAnterior - gastosMesAnterior

    // RF-11: Comparaciones calculadas
    val comparacionIngresos = CalculadoraComparacion.calcular(
        mesActual = "Oct 2026",
        mesAnterior = "Sep 2026",
        valorActual = ingresosMes,
        valorAnterior = ingresosMesAnterior,
        etiqueta = "Ingresos"
    )

    val comparacionGananciaNeta = CalculadoraComparacion.calcular(
        mesActual = "Oct 2026",
        mesAnterior = "Sep 2026",
        valorActual = gananciaNeta,
        valorAnterior = gananciaNetaMesAnterior,
        etiqueta = "Ganancia neta"
    )

    val comparacionGastos = CalculadoraComparacion.calcular(
        mesActual = "Oct 2026",
        mesAnterior = "Sep 2026",
        valorActual = gastosMes,
        valorAnterior = gastosMesAnterior,
        etiqueta = "Gastos"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground  // Importado desde MainDashboardScreen.kt (mismo package)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Ganancias del Mes",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ===== Tarjeta de ingresos (RF-10) =====
            CardGanancia(
                titulo = "Ingresos",
                valor = "$${"%,.0f".format(ingresosMes)}"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ===== Tarjeta de gastos (RF-10) =====
            CardGanancia(
                titulo = "Gastos",
                valor = "$${"%,.0f".format(gastosMes)}"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ===== Tarjeta de ganancia neta destacada (RF-10) =====
            CardGananciaDestacada(
                titulo = "Ganancia Neta",
                valor = "$${"%,.0f".format(gananciaNeta)}"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ===== Tarjeta de margen bruto (RF-10) =====
            CardGananciaDestacada(
                titulo = "Margen Bruto",
                valor = "${"%.1f".format(margenBruto)}%"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ===== RF-11: Sección de comparación mes actual vs anterior =====
            Text(
                text = "ESTE MES VS MES ANTERIOR",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Verificar si hay datos suficientes antes de mostrar comparaciones
            if (CalculadoraComparacion.hayDatosSuficientes(ingresosMes, ingresosMesAnterior)) {
                CardComparacion(comparacion = comparacionIngresos)
                Spacer(modifier = Modifier.height(8.dp))
                CardComparacion(comparacion = comparacionGananciaNeta)
                Spacer(modifier = Modifier.height(8.dp))
                CardComparacion(comparacion = comparacionGastos)
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Datos insuficientes para comparar",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ===== Componentes auxiliares =====

@Composable
private fun CardGanancia(titulo: String, valor: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = titulo, color = Color.White, fontSize = 16.sp)
            Text(text = valor, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CardGananciaDestacada(titulo: String, valor: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = titulo, color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valor,
                color = PrimaryBlue,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * RF-11: Card que muestra la comparación entre mes actual y mes anterior.
 * - Si porcentajeCambio es null → muestra "N/A" (mes anterior = 0)
 * - Color del porcentaje:
 *     verde si subió (positivo en Ingresos/Ganancia; negativo en Gastos)
 *     rojo si bajó (negativo en Ingresos/Ganancia; positivo en Gastos)
 */
@Composable
private fun CardComparacion(comparacion: ComparacionMensual) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Encabezado: etiqueta + valor actual
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comparacion.etiqueta,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${"%,.0f".format(comparacion.valorActual)}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Pie: valor anterior + porcentaje de cambio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${comparacion.mesAnterior}: $${"%,.0f".format(comparacion.valorAnterior)}",
                    color = Color.Gray,
                    fontSize = 13.sp
                )

                // RF-11: Si porcentajeCambio es null → "N/A"
                val textoPorcentaje: String
                val colorPorcentaje: Color

                if (comparacion.porcentajeCambio == null) {
                    textoPorcentaje = "N/A"
                    colorPorcentaje = Color.Gray
                } else {
                    val signo = if (comparacion.porcentajeCambio >= 0) "↑" else "↓"
                    textoPorcentaje = "$signo ${"%.1f".format(kotlin.math.abs(comparacion.porcentajeCambio))}%"

                    val subio = comparacion.porcentajeCambio >= 0
                    val esGasto = comparacion.etiqueta.contains("Gastos", ignoreCase = true)
                    colorPorcentaje = when {
                        subio && !esGasto -> Color(0xFF10B981)   // verde: ingresos/ganancia subieron
                        !subio && !esGasto -> Color(0xFFEF4444)  // rojo: ingresos/ganancia bajaron
                        subio && esGasto -> Color(0xFFEF4444)    // rojo: gastos subieron
                        else -> Color(0xFF10B981)                // verde: gastos bajaron
                    }
                }

                Text(
                    text = textoPorcentaje,
                    color = colorPorcentaje,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
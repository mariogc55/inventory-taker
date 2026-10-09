package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mariogc55.retrowave.tiendabarrioinventory.model.CalculadoraResumen
import com.mariogc55.retrowave.tiendabarrioinventory.model.ResumenMensual

/**
 * RF-12: Pantalla de Reportes.
 * Muestra:
 *   - Totales históricos (ingresos, ganancia neta, gastos, total ventas)
 *   - Gráfico de barras de los últimos 6 meses
 *   - Tabla "Resumen Mensual" con columnas: Mes | Ventas | Ingresos | Ganancia
 *
 * Reglas aplicadas:
 *   - Solo se listan meses con al menos 1 venta
 *   - Orden: más reciente a más antiguo
 *   - Si no hay transacciones → mensaje "No hay transacciones registradas aún"
 */
@Composable
fun ReportesScreen() {
    val historial = listOf(
        ResumenMensual("May", totalVentas = 0, totalIngresos = 0.0, ganancia = 0.0),
        ResumenMensual("Jun", totalVentas = 0, totalIngresos = 0.0, ganancia = 0.0),
        ResumenMensual("Jul", totalVentas = 0, totalIngresos = 0.0, ganancia = 0.0),
        ResumenMensual("Ago", totalVentas = 0, totalIngresos = 0.0, ganancia = 0.0),
        ResumenMensual("Sep", totalVentas = 34, totalIngresos = 712000.0, ganancia = 196000.0),
        ResumenMensual("Oct", totalVentas = 45, totalIngresos = 1000000.0, ganancia = -1005300.0)
    )

    val historialVisible = CalculadoraResumen.filtrarYOrdenar(historial)
    val ingresosTotales = CalculadoraResumen.calcularIngresosTotales(historial)
    val gananciaTotal = CalculadoraResumen.calcularGananciaTotal(historial)
    val gastosTotales = CalculadoraResumen.calcularGastosTotales(historial)
    val totalVentas = CalculadoraResumen.calcularTotalVentas(historial)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Reportes",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Historial completo",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                Surface(
                    color = Color(0xFF1E3A8A),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Excel",
                        color = PrimaryBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (CalculadoraResumen.hayTransacciones(historial)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    CardTotal(
                        titulo = "INGRESOS TOTALES",
                        valor = "$" + formatearK(ingresosTotales),
                        colorValor = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CardTotal(
                        titulo = "GANANCIA NETA",
                        valor = "$" + formatearNumero(gananciaTotal),
                        colorValor = if (gananciaTotal >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    CardTotal(
                        titulo = "GASTOS TOTALES",
                        valor = "$" + formatearK(gastosTotales),
                        colorValor = Color(0xFFEF4444),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CardTotal(
                        titulo = "TOTAL VENTAS",
                        valor = totalVentas.toString(),
                        colorValor = PrimaryBlue,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                GraficoUltimos6Meses(historial = historial)

                Spacer(modifier = Modifier.height(20.dp))

                TablaResumenMensual(historial = historialVisible)
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "No hay transacciones registradas aún",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CardTotal(
    titulo: String,
    valor: String,
    colorValor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = titulo,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = valor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colorValor
            )
        }
    }
}

@Composable
private fun GraficoUltimos6Meses(historial: List<ResumenMensual>) {
    val ultimos6 = historial.takeLast(6)
    val maxIngreso = (ultimos6.maxOfOrNull { it.totalIngresos } ?: 1.0).coerceAtLeast(1.0)
    val alturaMaxima: Dp = 100.dp

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ÚLTIMOS 6 MESES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(alturaMaxima),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ultimos6.forEach { resumen ->
                    val proporcion = (resumen.totalIngresos / maxIngreso).toFloat()
                    val alturaBarra: Dp = (alturaMaxima.value * proporcion).dp

                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(alturaBarra.coerceAtLeast(2.dp))
                            .background(
                                color = Color(0xFF10B981),
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ultimos6.forEach { resumen ->
                    Column(
                        modifier = Modifier.width(42.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = resumen.mes,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "$" + formatearK(resumen.totalIngresos),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TablaResumenMensual(historial: List<ResumenMensual>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "RESUMEN MENSUAL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Mes", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text("Ventas", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text("Ingresos", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
                Text("Ganancia", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.weight(1.2f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            historial.forEach { resumen ->
                FilaResumen(resumen = resumen)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun FilaResumen(resumen: ResumenMensual) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = resumen.mes,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = resumen.totalVentas.toString(),
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$" + formatearK(resumen.totalIngresos),
            color = Color(0xFF10B981),
            fontSize = 14.sp,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = "$" + formatearNumero(resumen.ganancia),
            color = if (resumen.ganancia >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
            fontSize = 14.sp,
            modifier = Modifier.weight(1.2f)
        )
    }
}

private fun formatearK(valor: Double): String {
    val absoluto = kotlin.math.abs(valor)
    return when {
        absoluto >= 1_000_000 -> "%.1fM".format(valor / 1_000_000)
        absoluto >= 1_000 -> "%.0fK".format(valor / 1_000)
        valor == 0.0 -> "0"
        else -> "%.0f".format(valor)
    }
}

private fun formatearNumero(valor: Double): String {
    return "%,.0f".format(valor)
}
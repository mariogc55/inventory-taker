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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mariogc55.retrowave.tiendabarrioinventory.model.CalculadoraComparacion
import com.mariogc55.retrowave.tiendabarrioinventory.model.CalculadoraGanancias
import com.mariogc55.retrowave.tiendabarrioinventory.model.ComparacionMensual
import com.mariogc55.retrowave.tiendabarrioinventory.model.ResumenGanancias
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

private val FigmaBgDark = Color(0xFF0B0F17)
private val FigmaCardBg = Color(0xFF151C28)
private val FigmaBluePrimary = Color(0xFF3B82F6)
private val FigmaGreenEmerald = Color(0xFF10B981)
private val FigmaRedError = Color(0xFFEF4444)
private val FigmaTextPrimary = Color(0xFFF1F5F9)
private val FigmaTextMuted = Color(0xFF94A3B8)

@Composable
fun GananciasScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val calculadora = remember { CalculadoraGanancias(context) }

    var resumen by remember {
        mutableStateOf(
            ResumenGanancias(
                ingresos = 0.0,
                gastos = 0.0,
                costoInventario = 0.0,
                gananciaNeta = 0.0,
                margenBruto = 0.0
            )
        )
    }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            cargando = true
            resumen = withContext(Dispatchers.IO) {
                try {
                    calculadora.calcular()
                } catch (_: Exception) {
                    ResumenGanancias(0.0, 0.0, 0.0, 0.0, 0.0)
                }
            }
            cargando = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = FigmaBgDark
    ) {
        if (cargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FigmaBluePrimary)
            }
        } else {
            ContenidoGananciasFigma(resumen = resumen)
        }
    }
}

@Composable
private fun ContenidoGananciasFigma(resumen: ResumenGanancias) {
    val gananciaBruta = (resumen.ingresos - resumen.costoInventario).coerceAtLeast(0.0)

    val ingresosMesAnterior = 0.0
    val gananciaBrutaMesAnterior = 0.0
    val gastosMesAnterior = 0.0

    val comparacionIngresos = CalculadoraComparacion.calcular(
        mesActual = "Este mes",
        mesAnterior = "Mes anterior",
        valorActual = resumen.ingresos,
        valorAnterior = ingresosMesAnterior,
        etiqueta = "Ingresos"
    )
    val comparacionGananciaBruta = CalculadoraComparacion.calcular(
        mesActual = "Este mes",
        mesAnterior = "Mes anterior",
        valorActual = gananciaBruta,
        valorAnterior = gananciaBrutaMesAnterior,
        etiqueta = "Ganancia bruta"
    )
    val comparacionGastos = CalculadoraComparacion.calcular(
        mesActual = "Este mes",
        mesAnterior = "Mes anterior",
        valorActual = resumen.gastos,
        valorAnterior = gastosMesAnterior,
        etiqueta = "Gastos"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = "Ganancias",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = FigmaTextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = FigmaCardBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "GANANCIA NETA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FigmaTextMuted,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = formatearMoneda(resumen.gananciaNeta),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (resumen.gananciaNeta < 0) FigmaRedError else FigmaTextPrimary
                    )
                    Text(
                        text = "Margen bruto: ${"%.0f".format(resumen.margenBruto)}%",
                        fontSize = 12.sp,
                        color = FigmaTextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TarjetaMetricaFigma(
                modifier = Modifier.weight(1f),
                titulo = "INGRESOS",
                valor = formatearMoneda(resumen.ingresos),
                valorColor = FigmaGreenEmerald,
                comparacion = comparacionIngresos,
                esGasto = false
            )
            TarjetaMetricaFigma(
                modifier = Modifier.weight(1f),
                titulo = "GANANCIA BRUTA",
                valor = formatearMoneda(gananciaBruta),
                valorColor = FigmaBluePrimary,
                comparacion = comparacionGananciaBruta,
                esGasto = false
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TarjetaMetricaFigma(
                modifier = Modifier.weight(1f),
                titulo = "GASTOS TOTALES",
                valor = formatearMoneda(resumen.gastos),
                valorColor = FigmaRedError,
                comparacion = comparacionGastos,
                esGasto = true
            )
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = FigmaCardBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "VENTAS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FigmaTextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "0",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FigmaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "0% vs ant.",
                        fontSize = 11.sp,
                        color = FigmaTextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ESTE MES VS MES ANTERIOR",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = FigmaTextMuted,
            letterSpacing = 0.6.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        BarraComparacionFigma(
            comparacion = comparacionIngresos,
            colorBarraActual = FigmaGreenEmerald
        )
        Spacer(modifier = Modifier.height(10.dp))
        BarraComparacionFigma(
            comparacion = comparacionGananciaBruta,
            colorBarraActual = FigmaBluePrimary
        )
        Spacer(modifier = Modifier.height(10.dp))
        BarraComparacionFigma(
            comparacion = comparacionGastos,
            colorBarraActual = FigmaRedError
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun TarjetaMetricaFigma(
    modifier: Modifier = Modifier,
    titulo: String,
    valor: String,
    valorColor: Color,
    comparacion: ComparacionMensual,
    esGasto: Boolean
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = FigmaCardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = titulo,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = FigmaTextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = valor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = valorColor
            )
            Spacer(modifier = Modifier.height(4.dp))

            val pct = comparacion.porcentajeCambio
            val textoDelta = if (pct == null) {
                "0% vs ant."
            } else {
                val flecha = if (pct >= 0) "↑" else "↓"
                "$flecha ${"%.0f".format(abs(pct))}% vs ant."
            }
            val colorDelta = when {
                pct == null -> FigmaTextMuted
                pct >= 0 && !esGasto -> FigmaGreenEmerald
                pct < 0 && !esGasto -> FigmaRedError
                pct >= 0 && esGasto -> FigmaRedError
                else -> FigmaGreenEmerald
            }

            Text(
                text = textoDelta,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorDelta
            )
        }
    }
}

@Composable
private fun BarraComparacionFigma(
    comparacion: ComparacionMensual,
    colorBarraActual: Color
) {
    val maxVal = maxOf(comparacion.valorActual, comparacion.valorAnterior, 1.0)
    val fraccionActual = (comparacion.valorActual / maxVal).toFloat().coerceIn(0f, 1f)
    val fraccionAnterior = (comparacion.valorAnterior / maxVal).toFloat().coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = FigmaCardBg),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comparacion.etiqueta,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FigmaTextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatearMoneda(comparacion.valorActual),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorBarraActual
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatearMoneda(comparacion.valorAnterior),
                        fontSize = 12.sp,
                        color = FigmaTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1E293B))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraccionActual)
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colorBarraActual)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1E293B))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraccionAnterior)
                        .height(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(FigmaTextMuted.copy(alpha = 0.55f))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                LeyendaPunto(color = colorBarraActual, texto = "Este mes")
                LeyendaPunto(color = FigmaTextMuted.copy(alpha = 0.6f), texto = "Mes anterior")
            }
        }
    }
}

@Composable
private fun LeyendaPunto(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = texto, fontSize = 11.sp, color = FigmaTextMuted)
    }
}

private fun formatearMoneda(valor: Double): String {
    if (valor == 0.0) return "$0"
    val signo = if (valor < 0) "-" else ""
    return "$signo$" + "%,.0f".format(abs(valor))
}
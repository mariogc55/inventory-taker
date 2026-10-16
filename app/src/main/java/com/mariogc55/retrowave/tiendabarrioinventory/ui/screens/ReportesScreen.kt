package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mariogc55.retrowave.tiendabarrioinventory.model.CalculadoraResumen
import com.mariogc55.retrowave.tiendabarrioinventory.model.FormatoExportacion
import com.mariogc55.retrowave.tiendabarrioinventory.model.GeneradorExcel
import com.mariogc55.retrowave.tiendabarrioinventory.model.ResultadoExportacion
import com.mariogc55.retrowave.tiendabarrioinventory.model.ResumenMensual
import com.mariogc55.retrowave.tiendabarrioinventory.model.SolicitudExportacion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mostrarDialogoExportar by remember { mutableStateOf(false) }
    var solicitudPendiente by remember { mutableStateOf<SolicitudExportacion?>(null) }

    val lanzadorGuardar = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(FormatoExportacion.EXCEL.mimeType)
    ) { uri ->
        val solicitud = solicitudPendiente
        solicitudPendiente = null
        if (uri == null || solicitud == null) {
            Toast.makeText(context, "✗ Exportación cancelada", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            val resultado = withContext(Dispatchers.IO) {
                try {
                    val filas = GeneradorExcel.filtrarPorRango(historial, solicitud.rangoFechas)
                    context.contentResolver.openOutputStream(uri)?.use { salida ->
                        GeneradorExcel.escribirXlsx(salida, filas)
                    } ?: throw IllegalStateException()
                    ResultadoExportacion.Exito(solicitud.nombreArchivo, filas.size)
                } catch (e: Exception) {
                    ResultadoExportacion.Error("✗ No se pudo generar el archivo")
                }
            }
            val mensaje = when (resultado) {
                is ResultadoExportacion.Exito -> "✓ Reporte listo: ${resultado.nombreArchivo} (${resultado.mesesExportados} meses)"
                is ResultadoExportacion.Error -> resultado.mensaje
            }
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
        }
    }

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
                    onClick = { mostrarDialogoExportar = true },
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

    if (mostrarDialogoExportar) {
        DialogoExportarExcel(
            meses = historial.map { it.mes },
            onDismiss = { mostrarDialogoExportar = false },
            onValidar = { inicio, fin -> GeneradorExcel.crearSolicitud(historial, inicio, fin) },
            onExportar = { solicitud ->
                mostrarDialogoExportar = false
                solicitudPendiente = solicitud
                lanzadorGuardar.launch(solicitud.nombreArchivo)
            }
        )
    }
}

@Composable
private fun DialogoExportarExcel(
    meses: List<String>,
    onDismiss: () -> Unit,
    onValidar: (Int?, Int?) -> Pair<SolicitudExportacion?, String?>,
    onExportar: (SolicitudExportacion) -> Unit
) {
    var mesInicio by remember { mutableStateOf<Int?>(null) }
    var mesFin by remember { mutableStateOf<Int?>(null) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = { Text("Exportar a Excel", color = Color.White) },
        text = {
            Column {
                Text(
                    text = "Seleccione el rango de meses a exportar (.xlsx)",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (mensajeError != null) {
                    Text(text = mensajeError!!, color = Color(0xFFEF4444), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                SelectorMes(
                    etiqueta = "Desde",
                    meses = meses,
                    seleccionado = mesInicio,
                    onSeleccionar = {
                        mesInicio = it
                        mensajeError = null
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                SelectorMes(
                    etiqueta = "Hasta",
                    meses = meses,
                    seleccionado = mesFin,
                    onSeleccionar = {
                        mesFin = it
                        mensajeError = null
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val (solicitud, error) = onValidar(mesInicio, mesFin)
                    if (solicitud != null) onExportar(solicitud) else mensajeError = "✗ $error"
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Exportar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    )
}

@Composable
private fun SelectorMes(
    etiqueta: String,
    meses: List<String>,
    seleccionado: Int?,
    onSeleccionar: (Int) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = etiqueta,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.width(60.dp)
        )
        Box(modifier = Modifier.weight(1f)) {
            OutlinedButton(
                onClick = { expandido = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = seleccionado?.let { meses[it] } ?: "Seleccionar mes",
                    color = if (seleccionado != null) PrimaryBlue else Color.Gray
                )
            }
            DropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false }
            ) {
                meses.forEachIndexed { indice, mes ->
                    DropdownMenuItem(
                        text = { Text(mes) },
                        onClick = {
                            onSeleccionar(indice)
                            expandido = false
                        }
                    )
                }
            }
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
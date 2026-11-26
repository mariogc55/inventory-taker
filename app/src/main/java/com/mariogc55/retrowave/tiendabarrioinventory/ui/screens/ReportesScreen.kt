package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mariogc55.retrowave.tiendabarrioinventory.model.CalculadoraGanancias
import com.mariogc55.retrowave.tiendabarrioinventory.model.FormatoExportacion
import com.mariogc55.retrowave.tiendabarrioinventory.model.GeneradorExcel
import com.mariogc55.retrowave.tiendabarrioinventory.model.ResumenGanancias
import com.mariogc55.retrowave.tiendabarrioinventory.model.ResumenMensual
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ReportesScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val calculadora = remember { CalculadoraGanancias(context) }

    var resumen by remember { mutableStateOf<ResumenGanancias?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    val resumenActual = rememberUpdatedState(resumen)

    fun cargarReporte() {
        scope.launch {
            cargando = true
            mensajeError = null
            try {
                val datos = withContext(Dispatchers.IO) { calculadora.calcular() }
                resumen = datos
            } catch (e: Exception) {
                mensajeError = "No se pudo conectar con el servidor"
            } finally {
                cargando = false
            }
        }
    }

    LaunchedEffect(Unit) { cargarReporte() }

    val lanzadorGuardar = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(FormatoExportacion.EXCEL.mimeType)
    ) { uri ->
        val r = resumenActual.value
        if (uri == null || r == null) {
            Toast.makeText(context, "✗ Exportación cancelada", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            val ok = withContext(Dispatchers.IO) {
                try {
                    val fila = ResumenMensual(
                        mes = "Resumen actual",
                        totalVentas = 0,
                        totalIngresos = r.ingresos,
                        ganancia = r.gananciaNeta
                    )
                    context.contentResolver.openOutputStream(uri)?.use { salida ->
                        GeneradorExcel.escribirXlsx(salida, listOf(fila))
                    } ?: throw IllegalStateException()
                    true
                } catch (e: Exception) {
                    false
                }
            }
            Toast.makeText(
                context,
                if (ok) "✓ Reporte exportado a Excel" else "✗ No se pudo generar el archivo",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = DarkBackground) {
        when {
            cargando -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PrimaryBlue)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Cargando desde el servidor...", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            }
            mensajeError != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✗ $mensajeError", color = Color(0xFFEF4444), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { cargarReporte() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) { Text("Reintentar") }
                    }
                }
            }
            resumen != null -> {
                ContenidoReportes(
                    resumen = resumen!!,
                    onExportar = { lanzadorGuardar.launch("reporte_inventoryt.xlsx") }
                )
            }
        }
    }
}

@Composable
private fun ContenidoReportes(resumen: ResumenGanancias, onExportar: () -> Unit) {
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
                Text("Reportes", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Resumen financiero actual", fontSize = 13.sp, color = Color.Gray)
            }
            Surface(
                onClick = onExportar,
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

        Row(modifier = Modifier.fillMaxWidth()) {
            CardTotal(
                titulo = "INGRESOS (VENTAS)",
                valor = "$" + "%,.0f".format(resumen.ingresos),
                colorValor = Color(0xFF10B981),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            CardTotal(
                titulo = "GASTOS TOTALES",
                valor = "$" + "%,.0f".format(resumen.gastos),
                colorValor = Color(0xFFEF4444),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            CardTotal(
                titulo = "GANANCIA NETA",
                valor = "$" + "%,.0f".format(resumen.gananciaNeta),
                colorValor = if (resumen.gananciaNeta >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            CardTotal(
                titulo = "MARGEN BRUTO",
                valor = "%.1f".format(resumen.margenBruto) + "%",
                colorValor = PrimaryBlue,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "DETALLE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))
                FilaDetalle("Ingresos por ventas", "$" + "%,.0f".format(resumen.ingresos), Color(0xFF10B981))
                Spacer(modifier = Modifier.height(8.dp))
                FilaDetalle("Costo del inventario", "$" + "%,.0f".format(resumen.costoInventario), Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                FilaDetalle("Gastos registrados", "$" + "%,.0f".format(resumen.gastos), Color(0xFFEF4444))
                Spacer(modifier = Modifier.height(8.dp))
                FilaDetalle(
                    "Ganancia neta",
                    "$" + "%,.0f".format(resumen.gananciaNeta),
                    if (resumen.gananciaNeta >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "El historial mensual se habilitará cuando se registren cierres por mes. Este resumen refleja los datos reales actuales del servidor.",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FilaDetalle(etiqueta: String, valor: String, colorValor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, color = Color(0xFF94A3B8), fontSize = 14.sp)
        Text(valor, color = colorValor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
            Text(text = titulo, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = valor, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colorValor)
        }
    }
}
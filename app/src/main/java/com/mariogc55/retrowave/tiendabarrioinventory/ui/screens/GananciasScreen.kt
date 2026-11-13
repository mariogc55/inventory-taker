package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun GananciasScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val calculadora = remember { CalculadoraGanancias(context) }

    var resumen by remember { mutableStateOf<ResumenGanancias?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    fun cargarGanancias() {
        scope.launch {
            cargando = true
            mensajeError = null
            try {
                val datos = withContext(Dispatchers.IO) {
                    calculadora.calcular()
                }
                resumen = datos
            } catch (e: Exception) {
                mensajeError = "No se pudo conectar con el servidor"
            } finally {
                cargando = false
            }
        }
    }

    LaunchedEffect(Unit) {
        cargarGanancias()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
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
                            onClick = { cargarGanancias() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            resumen != null -> {
                ContenidoGanancias(resumen = resumen!!)
            }
        }
    }
}

@Composable
private fun ContenidoGanancias(resumen: ResumenGanancias) {
    val ingresosMesAnterior = 2400000.0
    val gastosMesAnterior = 1200000.0
    val gananciaNetaMesAnterior = ingresosMesAnterior - gastosMesAnterior

    val comparacionIngresos = CalculadoraComparacion.calcular(
        mesActual = "Este mes",
        mesAnterior = "Mes anterior",
        valorActual = resumen.ingresos,
        valorAnterior = ingresosMesAnterior,
        etiqueta = "Ingresos"
    )

    val comparacionGananciaNeta = CalculadoraComparacion.calcular(
        mesActual = "Este mes",
        mesAnterior = "Mes anterior",
        valorActual = resumen.gananciaNeta,
        valorAnterior = gananciaNetaMesAnterior,
        etiqueta = "Ganancia neta"
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
            .padding(16.dp)
    ) {
        Text(
            text = "Ganancias del Mes",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        CardGanancia(titulo = "Ingresos", valor = "$" + "%,.0f".format(resumen.ingresos))

        Spacer(modifier = Modifier.height(8.dp))

        CardGanancia(titulo = "Gastos", valor = "$" + "%,.0f".format(resumen.gastos))

        Spacer(modifier = Modifier.height(16.dp))

        CardGananciaDestacada(titulo = "Ganancia Neta", valor = "$" + "%,.0f".format(resumen.gananciaNeta))

        Spacer(modifier = Modifier.height(8.dp))

        CardGananciaDestacada(titulo = "Margen Bruto", valor = "%.1f".format(resumen.margenBruto) + "%")

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "ESTE MES VS MES ANTERIOR",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (CalculadoraComparacion.hayDatosSuficientes(resumen.ingresos, ingresosMesAnterior)) {
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
                    text = "$" + "%,.0f".format(comparacion.valorActual),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = comparacion.mesAnterior + ": $" + "%,.0f".format(comparacion.valorAnterior),
                    color = Color.Gray,
                    fontSize = 13.sp
                )

                val textoPorcentaje: String
                val colorPorcentaje: Color

                if (comparacion.porcentajeCambio == null) {
                    textoPorcentaje = "N/A"
                    colorPorcentaje = Color.Gray
                } else {
                    val signo = if (comparacion.porcentajeCambio >= 0) "↑" else "↓"
                    textoPorcentaje = signo + " " + "%.1f".format(kotlin.math.abs(comparacion.porcentajeCambio)) + "%"

                    val subio = comparacion.porcentajeCambio >= 0
                    val esGasto = comparacion.etiqueta.contains("Gastos", ignoreCase = true)
                    colorPorcentaje = when {
                        subio && !esGasto -> Color(0xFF10B981)
                        !subio && !esGasto -> Color(0xFFEF4444)
                        subio && esGasto -> Color(0xFFEF4444)
                        else -> Color(0xFF10B981)
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
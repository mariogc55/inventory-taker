package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mariogc55.retrowave.tiendabarrioinventory.model.Gasto
import com.mariogc55.retrowave.tiendabarrioinventory.model.GastoDto
import com.mariogc55.retrowave.tiendabarrioinventory.model.GastoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ScreenDarkBg = Color(0xFF0B0F17)
private val ScreenCardBg = Color(0xFF151C28)
private val ScreenPrimaryBlue = Color(0xFF3B82F6)
private val ScreenTextColor = Color(0xFFF1F5F9)

private val categoriasGasto = listOf("Arriendo", "Servicios", "Compras", "Insumos", "Otros")

@Composable
fun GastosScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val gastosList = remember { mutableStateListOf<Gasto>() }
    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    fun cargarGastos() {
        scope.launch {
            cargando = true
            mensajeError = null
            try {
                val gastos = withContext(Dispatchers.IO) {
                    GastoRepository.obtenerGastos()
                }
                gastosList.clear()
                gastosList.addAll(gastos.map { it.toGasto() })
            } catch (e: Exception) {
                mensajeError = "No se pudo conectar con el servidor"
            } finally {
                cargando = false
            }
        }
    }

    LaunchedEffect(Unit) {
        cargarGastos()
    }

    val totalGastos = gastosList.sumOf { it.monto }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Gastos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ScreenTextColor
            )
            Text(
                text = "Total del mes: $" + "%,.0f".format(totalGastos),
                fontSize = 14.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                cargando -> {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = ScreenPrimaryBlue)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Cargando desde el servidor...", color = Color(0xFF64748B), fontSize = 13.sp)
                        }
                    }
                }
                mensajeError != null -> {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("✗ $mensajeError", color = Color(0xFFEF4444), fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { cargarGastos() },
                                colors = ButtonDefaults.buttonColors(containerColor = ScreenPrimaryBlue)
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                gastosList.isEmpty() -> {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay gastos registrados", color = Color(0xFF64748B), fontSize = 14.sp)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 90.dp)
                    ) {
                        items(gastosList) { gasto ->
                            TarjetaGasto(gasto = gasto)
                        }
                    }
                }
            }
        }

        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .fillMaxWidth(0.88f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ScreenPrimaryBlue)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Registrar Gasto", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }

    if (showAddDialog) {
        DialogoRegistrarGasto(
            onDismiss = { showAddDialog = false },
            onGuardar = { dto ->
                scope.launch {
                    val ok = withContext(Dispatchers.IO) {
                        GastoRepository.agregarGasto(dto)
                    }
                    showAddDialog = false
                    if (ok) {
                        Toast.makeText(context, "✓ Gasto guardado en el servidor", Toast.LENGTH_SHORT).show()
                        cargarGastos()
                    } else {
                        Toast.makeText(context, "✗ No se pudo guardar en el servidor", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}

@Composable
private fun TarjetaGasto(gasto: Gasto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ScreenCardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(gasto.descripcion, color = ScreenTextColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(gasto.categoria, color = Color(0xFF94A3B8), fontSize = 13.sp)
            }
            Text(
                text = "$" + "%,.0f".format(gasto.monto),
                color = Color(0xFFEF4444),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DialogoRegistrarGasto(
    onDismiss: () -> Unit,
    onGuardar: (GastoDto) -> Unit
) {
    var descripcion by remember { mutableStateOf("") }
    var montoText by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expandido by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ScreenCardBg,
        title = { Text("Registrar Gasto", color = ScreenTextColor) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = Color(0xFFEF4444), fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { if (it.length <= 200) descripcion = it },
                    label = { Text("Descripción") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = ScreenTextColor, unfocusedTextColor = ScreenTextColor)
                )

                OutlinedTextField(
                    value = montoText,
                    onValueChange = { montoText = it },
                    label = { Text("Monto ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = ScreenTextColor, unfocusedTextColor = ScreenTextColor)
                )

                Box {
                    OutlinedButton(
                        onClick = { expandido = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = categoria ?: "Seleccionar categoría",
                            color = if (categoria != null) ScreenPrimaryBlue else Color(0xFF64748B)
                        )
                    }
                    DropdownMenu(
                        expanded = expandido,
                        onDismissRequest = { expandido = false }
                    ) {
                        categoriasGasto.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    categoria = cat
                                    expandido = false
                                    errorMessage = null
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val monto = montoText.toDoubleOrNull() ?: 0.0
                    when {
                        descripcion.isBlank() -> errorMessage = "La descripción es obligatoria"
                        monto <= 0 -> errorMessage = "El monto debe ser mayor a 0"
                        categoria == null -> errorMessage = "Seleccione una categoría"
                        else -> onGuardar(
                            GastoDto(
                                descripcion = descripcion,
                                monto = monto,
                                categoria = categoria!!
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ScreenPrimaryBlue)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF64748B))
            }
        }
    )
}
package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    var cargando by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var gastoAEditar by remember { mutableStateOf<Gasto?>(null) }
    var gastoAEliminar by remember { mutableStateOf<Gasto?>(null) }

    fun cargarGastos() {
        scope.launch {
            cargando = true
            val gastosRemotos = withContext(Dispatchers.IO) {
                try { GastoRepository.obtenerGastos() } catch (e: Exception) { emptyList() }
            }
            if (gastosRemotos.isNotEmpty()) {
                gastosList.clear()
                gastosList.addAll(gastosRemotos.map { it.toGasto() })
            }
            cargando = false
        }
    }

    LaunchedEffect(Unit) { cargarGastos() }

    val totalGastos = gastosList.sumOf { it.monto }

    Box(modifier = Modifier.fillMaxSize().background(ScreenDarkBg)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Gastos", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ScreenTextColor)
            Text("Total del mes: $" + "%,.0f".format(totalGastos), fontSize = 14.sp, color = Color(0xFF94A3B8))

            Spacer(modifier = Modifier.height(16.dp))

            when {
                cargando && gastosList.isEmpty() -> {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = ScreenPrimaryBlue)
                            Spacer(Modifier.height(12.dp))
                            Text("Cargando desde el servidor...", color = Color(0xFF64748B), fontSize = 13.sp)
                        }
                    }
                }
                gastosList.isEmpty() -> {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
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
                            TarjetaGasto(
                                gasto = gasto,
                                onEditar = { gastoAEditar = gasto },
                                onEliminar = { gastoAEliminar = gasto }
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp).fillMaxWidth(0.88f).height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ScreenPrimaryBlue)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Registrar Gasto", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }

    if (showAddDialog) {
        DialogoGasto(
            gastoInicial = null,
            onDismiss = { showAddDialog = false },
            onGuardar = { dto ->
                showAddDialog = false
                scope.launch {
                    val ok = withContext(Dispatchers.IO) {
                        try { GastoRepository.agregarGasto(dto) } catch (e: Exception) { false }
                    }
                    Toast.makeText(context, if (ok) "✓ Gasto guardado" else "✗ No se pudo guardar en el servidor", Toast.LENGTH_SHORT).show()
                    cargarGastos()
                }
            }
        )
    }

    gastoAEditar?.let { g ->
        DialogoGasto(
            gastoInicial = g,
            onDismiss = { gastoAEditar = null },
            onGuardar = { dto ->
                gastoAEditar = null
                scope.launch {
                    val ok = withContext(Dispatchers.IO) {
                        try { GastoRepository.actualizarGasto(g.id.toLong(), dto) } catch (e: Exception) { false }
                    }
                    Toast.makeText(context, if (ok) "✓ Gasto actualizado" else "✗ No se pudo actualizar", Toast.LENGTH_SHORT).show()
                    cargarGastos()
                }
            }
        )
    }

    gastoAEliminar?.let { g ->
        AlertDialog(
            onDismissRequest = { gastoAEliminar = null },
            containerColor = ScreenCardBg,
            title = { Text("Eliminar gasto", color = ScreenTextColor) },
            text = { Text("¿Seguro que quieres eliminar '${g.descripcion}'?", color = Color(0xFF94A3B8)) },
            confirmButton = {
                TextButton(onClick = {
                    gastoAEliminar = null
                    scope.launch {
                        val ok = withContext(Dispatchers.IO) {
                            try { GastoRepository.eliminarGasto(g.id.toLong()) } catch (e: Exception) { false }
                        }
                        Toast.makeText(context, if (ok) "✓ Gasto eliminado" else "✗ No se pudo eliminar", Toast.LENGTH_SHORT).show()
                        cargarGastos()
                    }
                }) { Text("Eliminar", color = Color(0xFFEF4444)) }
            },
            dismissButton = {
                TextButton(onClick = { gastoAEliminar = null }) { Text("Cancelar", color = Color(0xFF64748B)) }
            }
        )
    }
}

@Composable
private fun TarjetaGasto(gasto: Gasto, onEditar: () -> Unit, onEliminar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEditar() },
        colors = CardDefaults.cardColors(containerColor = ScreenCardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(gasto.descripcion, color = ScreenTextColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(gasto.categoria, color = Color(0xFF94A3B8), fontSize = 13.sp)
            }
            Text("$" + "%,.0f".format(gasto.monto), color = Color(0xFFEF4444), fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = onEliminar,
                modifier = Modifier.size(36.dp).background(Color(0xFF2D1515), RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun DialogoGasto(
    gastoInicial: Gasto?,
    onDismiss: () -> Unit,
    onGuardar: (GastoDto) -> Unit
) {
    var descripcion by remember { mutableStateOf(gastoInicial?.descripcion ?: "") }
    var montoText by remember { mutableStateOf(gastoInicial?.monto?.let { "%.0f".format(it) } ?: "") }
    var categoria by remember { mutableStateOf(gastoInicial?.categoria) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expandido by remember { mutableStateOf(false) }

    val esEdicion = gastoInicial != null

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ScreenCardBg,
        title = { Text(if (esEdicion) "Editar Gasto" else "Registrar Gasto", color = ScreenTextColor) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (errorMessage != null) {
                    Text(errorMessage!!, color = Color(0xFFEF4444), fontSize = 12.sp)
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
                    OutlinedButton(onClick = { expandido = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(categoria ?: "Seleccionar categoría", color = if (categoria != null) ScreenPrimaryBlue else Color(0xFF64748B))
                    }
                    DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                        categoriasGasto.forEach { cat ->
                            DropdownMenuItem(text = { Text(cat) }, onClick = {
                                categoria = cat; expandido = false; errorMessage = null
                            })
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
                        else -> onGuardar(GastoDto(descripcion = descripcion, monto = monto, categoria = categoria!!))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ScreenPrimaryBlue)
            ) { Text(if (esEdicion) "Actualizar" else "Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = Color(0xFF64748B)) }
        }
    )
}

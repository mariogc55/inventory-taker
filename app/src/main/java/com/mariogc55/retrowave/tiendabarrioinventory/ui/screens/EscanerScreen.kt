package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.mariogc55.retrowave.tiendabarrioinventory.model.AnalizadorCodigo
import com.mariogc55.retrowave.tiendabarrioinventory.model.CodigoEscaneado
import kotlinx.coroutines.delay
import java.util.concurrent.Executors

@Composable
fun EscanerScreen(
    onCodigoDetectado: (CodigoEscaneado) -> Unit,
    onCerrar: () -> Unit
) {
    val context = LocalContext.current

    var permisoConcedido by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val lanzadorPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        permisoConcedido = concedido
    }

    LaunchedEffect(Unit) {
        if (!permisoConcedido) lanzadorPermiso.launch(Manifest.permission.CAMERA)
    }

    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            if (permisoConcedido) {
                VistaCamara(onCodigoDetectado = onCodigoDetectado)
            } else {
                SinPermisoCamara(onSolicitar = { lanzadorPermiso.launch(Manifest.permission.CAMERA) })
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Escanear código",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onCerrar) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun VistaCamara(onCodigoDetectado: (CodigoEscaneado) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val ejecutor = remember { Executors.newSingleThreadExecutor() }

    var intento by remember { mutableIntStateOf(0) }
    var mostrarReintento by remember { mutableStateOf(false) }
    var codigoEncontrado by remember { mutableStateOf<CodigoEscaneado?>(null) }

    val analizador = remember {
        AnalizadorCodigo { codigo -> codigoEncontrado = codigo }
    }

    LaunchedEffect(codigoEncontrado) {
        val codigo = codigoEncontrado
        if (codigo != null) {
            delay(700)
            onCodigoDetectado(codigo)
        }
    }

    LaunchedEffect(intento) {
        mostrarReintento = false
        analizador.reiniciar()
        delay(CodigoEscaneado.TIEMPO_REINTENTO_MS)
        if (codigoEncontrado == null) mostrarReintento = true
    }

    DisposableEffect(Unit) {
        onDispose {
            ProcessCameraProvider.getInstance(context).get().unbindAll()
            analizador.cerrar()
            ejecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val vistaPrevia = PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
                val proveedorFuturo = ProcessCameraProvider.getInstance(ctx)
                proveedorFuturo.addListener({
                    val proveedor = proveedorFuturo.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(vistaPrevia.surfaceProvider)
                    }
                    val analisis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { it.setAnalyzer(ejecutor, analizador) }
                    proveedor.unbindAll()
                    proveedor.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analisis
                    )
                }, ContextCompat.getMainExecutor(ctx))
                vistaPrevia
            }
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(260.dp, 180.dp)
                .border(2.dp, PrimaryBlue, RoundedCornerShape(16.dp))
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val codigo = codigoEncontrado
            when {
                codigo != null -> TarjetaEstado(
                    titulo = "✓ Código ${codigo.tipo.etiqueta} detectado",
                    detalle = "${codigo.valor} · en ${codigo.tiempoEnSegundos}",
                    color = Color(0xFF10B981)
                )
                mostrarReintento -> {
                    TarjetaEstado(
                        titulo = "✗ No se detectó ningún código",
                        detalle = "Acerque el código al recuadro, con buena luz, e intente de nuevo",
                        color = Color(0xFFEF4444)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { intento++ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Reintentar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                else -> TarjetaEstado(
                    titulo = "Buscando código...",
                    detalle = "Apunte al código de barras (EAN-13) o QR del producto",
                    color = PrimaryBlue
                )
            }
        }
    }
}

@Composable
private fun TarjetaEstado(titulo: String, detalle: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = titulo, color = color, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = detalle, color = Color.Gray, fontSize = 13.sp)
        }
    }
}

@Composable
private fun SinPermisoCamara(onSolicitar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "✗ Se requiere permiso de cámara para escanear",
            color = Color(0xFFEF4444),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onSolicitar,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Conceder permiso", color = Color.White)
        }
    }
}
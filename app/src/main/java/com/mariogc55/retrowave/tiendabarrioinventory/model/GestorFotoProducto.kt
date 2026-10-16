package com.mariogc55.retrowave.tiendabarrioinventory.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream

object GestorFotoProducto {

    const val TAMANIO_MAXIMO_PX = 512
    const val CALIDAD_JPEG = 80

    fun crearArchivoTemporal(context: Context): File {
        val directorio = File(context.cacheDir, "fotos_productos")
        if (!directorio.exists()) directorio.mkdirs()
        return File(directorio, "temp_${System.currentTimeMillis()}.jpg")
    }

    fun uriParaArchivo(context: Context, archivo: File): Uri {
        return androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )
    }

    fun procesarYGuardar(context: Context, idProducto: String, archivoTemporal: File): String? {
        val original = BitmapFactory.decodeFile(archivoTemporal.absolutePath) ?: return null
        val rotado = corregirOrientacion(archivoTemporal.absolutePath, original)
        val redimensionado = redimensionar(rotado)

        val directorio = File(context.filesDir, "productos")
        if (!directorio.exists()) directorio.mkdirs()
        val destino = File(directorio, "producto_$idProducto.jpg")

        FileOutputStream(destino).use { salida ->
            redimensionado.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, salida)
        }

        if (archivoTemporal.exists()) archivoTemporal.delete()
        return destino.absolutePath
    }

    fun redimensionar(bitmap: Bitmap): Bitmap {
        val ancho = bitmap.width
        val alto = bitmap.height
        if (ancho <= TAMANIO_MAXIMO_PX && alto <= TAMANIO_MAXIMO_PX) return bitmap

        val proporcion = ancho.toFloat() / alto.toFloat()
        val (nuevoAncho, nuevoAlto) = if (ancho >= alto) {
            TAMANIO_MAXIMO_PX to (TAMANIO_MAXIMO_PX / proporcion).toInt()
        } else {
            (TAMANIO_MAXIMO_PX * proporcion).toInt() to TAMANIO_MAXIMO_PX
        }
        return Bitmap.createScaledBitmap(bitmap, nuevoAncho, nuevoAlto, true)
    }

    private fun corregirOrientacion(ruta: String, bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(ruta)
        val orientacion = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        val grados = when (orientacion) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        if (grados == 0f) return bitmap
        val matriz = android.graphics.Matrix().apply { postRotate(grados) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
    }
}
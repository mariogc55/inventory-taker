package com.mariogc55.retrowave.tiendabarrioinventory.model

import android.content.Context
import com.mariogc55.retrowave.tiendabarrioinventory.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
private data class ActualizacionStock(val stock: Int)

object VentaRepository {

    private val baseUrl = "${BuildConfig.SUPABASE_URL}/rest/v1"
    private val apiKey = BuildConfig.SUPABASE_KEY

    private val cliente = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                explicitNulls = false
            })
        }
    }

    suspend fun obtenerVentas(context: Context? = null): List<VentaDto> {
        val dao = context?.let { AppDatabase.getDatabase(it).ventaDao() }

        return try {
            val remotos: List<VentaDto> = cliente.get("$baseUrl/ventas") {
                header("apikey", apiKey)
                header("Authorization", "Bearer $apiKey")
                parameter("select", "*")
                parameter("order", "id.desc")
            }.body()

            if (dao != null) {
                val localesPrevias = dao.obtenerTodas()
                val noSincronizadas = localesPrevias.filter { !it.sincronizado }.map { it.toDto() }

                dao.limpiarSincronizadas()
                for (dto in remotos) {
                    dao.insertar(
                        VentaEntity(
                            remoteId = dto.id,
                            productId = dto.productId ?: "",
                            productName = dto.productName,
                            quantity = dto.quantity,
                            unitPrice = dto.unitPrice,
                            totalPrice = dto.totalPrice,
                            createdAt = dto.createdAt ?: "",
                            sincronizado = true
                        )
                    )
                }
                (noSincronizadas + remotos)
            } else {
                remotos
            }
        } catch (_: Exception) {
            dao?.obtenerTodas()?.map { it.toDto() } ?: emptyList()
        }
    }

    suspend fun agregarVenta(venta: VentaDto, context: Context? = null): Boolean {
        val dao = context?.let { AppDatabase.getDatabase(it).ventaDao() }
        val fechaActual = venta.createdAt ?: SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss",
            Locale.getDefault()
        ).format(Date())
        val ventaConFecha = venta.copy(createdAt = fechaActual)

        var sincronizada = false
        try {
            val respuesta = cliente.post("$baseUrl/ventas") {
                header("apikey", apiKey)
                header("Authorization", "Bearer $apiKey")
                header("Prefer", "return=minimal")
                contentType(ContentType.Application.Json)
                setBody(ventaConFecha)
            }
            sincronizada = respuesta.status.value in 200..299
        } catch (_: Exception) {
            sincronizada = false
        }

        dao?.insertar(
            VentaEntity(
                remoteId = ventaConFecha.id,
                productId = ventaConFecha.productId ?: "",
                productName = ventaConFecha.productName,
                quantity = ventaConFecha.quantity,
                unitPrice = ventaConFecha.unitPrice,
                totalPrice = ventaConFecha.totalPrice,
                createdAt = fechaActual,
                sincronizado = sincronizada
            )
        )

        return true
    }

    suspend fun descontarStock(productoId: String, nuevoStock: Int, context: Context? = null): Boolean {
        val stockLimpio = nuevoStock.coerceAtLeast(0)
        if (context != null && productoId.isNotBlank()) {
            try {
                val productDao = AppDatabase.getDatabase(context).productDao()
                val locales = productDao.obtenerTodos()
                val objetivo = locales.find {
                    (it.remoteId?.toString() == productoId) || (it.localId.toString() == productoId)
                }
                if (objetivo != null) {
                    productDao.insertar(objetivo.copy(stock = stockLimpio))
                }
            } catch (_: Exception) {
            }
        }

        if (productoId.isBlank()) return false
        return try {
            val respuesta = cliente.patch("$baseUrl/productos?id=eq.$productoId") {
                header("apikey", apiKey)
                header("Authorization", "Bearer $apiKey")
                header("Prefer", "return=minimal")
                contentType(ContentType.Application.Json)
                setBody(ActualizacionStock(stockLimpio))
            }
            respuesta.status.value in 200..299
        } catch (_: Exception) {
            false
        }
    }
}
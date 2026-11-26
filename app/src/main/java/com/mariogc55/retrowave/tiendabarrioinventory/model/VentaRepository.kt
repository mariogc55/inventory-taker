package com.mariogc55.retrowave.tiendabarrioinventory.model

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

    suspend fun obtenerVentas(): List<VentaDto> {
        return cliente.get("$baseUrl/ventas") {
            header("apikey", apiKey)
            header("Authorization", "Bearer $apiKey")
            parameter("select", "*")
            parameter("order", "id.desc")
        }.body()
    }

    suspend fun agregarVenta(venta: VentaDto): Boolean {
        return try {
            val respuesta = cliente.post("$baseUrl/ventas") {
                header("apikey", apiKey)
                header("Authorization", "Bearer $apiKey")
                header("Prefer", "return=minimal")
                contentType(ContentType.Application.Json)
                setBody(venta)
            }
            respuesta.status.value in 200..299
        } catch (_: Exception) {
            false
        }
    }

    suspend fun descontarStock(productoId: String, nuevoStock: Int): Boolean {
        return try {
            val respuesta = cliente.patch("$baseUrl/productos?id=eq.$productoId") {
                header("apikey", apiKey)
                header("Authorization", "Bearer $apiKey")
                header("Prefer", "return=minimal")
                contentType(ContentType.Application.Json)
                setBody(ActualizacionStock(nuevoStock))
            }
            respuesta.status.value in 200..299
        } catch (_: Exception) {
            false
        }
    }
}

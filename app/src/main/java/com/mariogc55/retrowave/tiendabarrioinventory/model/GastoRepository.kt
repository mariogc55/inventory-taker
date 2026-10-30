package com.mariogc55.retrowave.tiendabarrioinventory.model

import com.mariogc55.retrowave.tiendabarrioinventory.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object GastoRepository {

    private val baseUrl = "${BuildConfig.SUPABASE_URL}/rest/v1"
    private val apiKey = BuildConfig.SUPABASE_KEY

    private val cliente = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun obtenerGastos(): List<GastoDto> {
        return cliente.get("$baseUrl/gastos") {
            header("apikey", apiKey)
            header("Authorization", "Bearer $apiKey")
            parameter("select", "*")
            parameter("order", "id.asc")
        }.body()
    }

    suspend fun agregarGasto(gasto: GastoDto): Boolean {
        val respuesta = cliente.post("$baseUrl/gastos") {
            header("apikey", apiKey)
            header("Authorization", "Bearer $apiKey")
            header("Prefer", "return=minimal")
            contentType(ContentType.Application.Json)
            setBody(gasto)
        }
        return respuesta.status.value in 200..299
    }
}
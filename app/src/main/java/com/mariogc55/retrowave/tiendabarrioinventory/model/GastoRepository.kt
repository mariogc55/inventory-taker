package com.mariogc55.retrowave.tiendabarrioinventory.model

import android.content.Context
import com.mariogc55.retrowave.tiendabarrioinventory.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
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
                explicitNulls = false
            })
        }
    }

    suspend fun obtenerGastos(context: Context? = null): List<GastoDto> {
        val dao = context?.let { AppDatabase.getDatabase(it).gastoDao() }

        return try {
            val remotos: List<GastoDto> = cliente.get("$baseUrl/gastos") {
                header("apikey", apiKey)
                header("Authorization", "Bearer $apiKey")
                parameter("select", "*")
                parameter("order", "id.asc")
            }.body()

            if (dao != null) {
                val localesActuales = dao.obtenerTodos()
                val pendientes = localesActuales.filter { !it.sincronizado }

                if (remotos.isEmpty() && pendientes.isNotEmpty()) {
                    return pendientes.map { it.toDto() }
                }

                dao.limpiarSincronizados()
                for (dto in remotos) {
                    dao.insertar(
                        GastoEntity(
                            remoteId = dto.id,
                            descripcion = dto.descripcion,
                            monto = dto.monto,
                            categoria = dto.categoria,
                            sincronizado = true
                        )
                    )
                }
                val pendientesDto = pendientes.map { it.toDto() }
                (pendientesDto + remotos)
            } else {
                remotos
            }
        } catch (_: Exception) {
            dao?.obtenerTodos()?.map { it.toDto() } ?: emptyList()
        }
    }

    suspend fun agregarGasto(gasto: GastoDto, context: Context? = null): Boolean {
        val dao = context?.let { AppDatabase.getDatabase(it).gastoDao() }

        val localIdInsertado = dao?.insertar(
            GastoEntity(
                remoteId = gasto.id,
                descripcion = gasto.descripcion,
                monto = gasto.monto,
                categoria = gasto.categoria,
                sincronizado = false
            )
        ) ?: 0L

        var sincronizado = false
        try {
            val respuesta = cliente.post("$baseUrl/gastos") {
                header("apikey", apiKey)
                header("Authorization", "Bearer $apiKey")
                header("Prefer", "return=minimal")
                contentType(ContentType.Application.Json)
                setBody(gasto)
            }
            sincronizado = respuesta.status.value in 200..299
        } catch (_: Exception) {
            sincronizado = false
        }

        if (sincronizado && dao != null && localIdInsertado > 0L) {
            dao.actualizar(
                localId = localIdInsertado,
                remoteId = gasto.id,
                descripcion = gasto.descripcion,
                monto = gasto.monto,
                categoria = gasto.categoria,
                sincronizado = true
            )
        }

        return localIdInsertado > 0L || sincronizado
    }

    suspend fun actualizarGasto(id: Long, gasto: GastoDto, context: Context? = null): Boolean {
        val dao = context?.let { AppDatabase.getDatabase(it).gastoDao() }
        var sincronizado = false

        if (id > 0L) {
            try {
                val respuesta = cliente.patch("$baseUrl/gastos?id=eq.$id") {
                    header("apikey", apiKey)
                    header("Authorization", "Bearer $apiKey")
                    header("Prefer", "return=minimal")
                    contentType(ContentType.Application.Json)
                    setBody(gasto)
                }
                sincronizado = respuesta.status.value in 200..299
            } catch (_: Exception) {
                sincronizado = false
            }
        }

        dao?.actualizar(
            localId = id,
            remoteId = if (id > 0L) id else null,
            descripcion = gasto.descripcion,
            monto = gasto.monto,
            categoria = gasto.categoria,
            sincronizado = sincronizado
        )

        return true
    }

    suspend fun eliminarGasto(id: Long, context: Context? = null): Boolean {
        val dao = context?.let { AppDatabase.getDatabase(it).gastoDao() }
        dao?.eliminarPorId(id)

        if (id <= 0L) {
            return true
        }

        return try {
            val respuesta = cliente.delete("$baseUrl/gastos?id=eq.$id") {
                header("apikey", apiKey)
                header("Authorization", "Bearer $apiKey")
                header("Prefer", "return=minimal")
            }
            respuesta.status.value in 200..299 || dao != null
        } catch (_: Exception) {
            dao != null
        }
    }
}
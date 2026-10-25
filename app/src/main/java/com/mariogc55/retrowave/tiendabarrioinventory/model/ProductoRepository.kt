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
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ProductoRepository(context: Context) {
    private val productDao = AppDatabase.getDatabase(context).productDao()

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

    suspend fun obtenerProductos(): List<ProductEntity> {
        return try {
            val remoteProducts: List<ProductoDto> = cliente.get("$baseUrl/productos") {
                header("apikey", apiKey)
                header("Authorization", "Bearer $apiKey")
                parameter("select", "*")
                parameter("order", "id.asc")
            }.body()

            productDao.limpiarTodo()
            for (dto in remoteProducts) {
                productDao.insertar(
                    ProductEntity(
                        remoteId = dto.id,
                        name = dto.name,
                        category = dto.category,
                        stock = dto.stock,
                        purchasePrice = dto.purchasePrice,
                        sellingPrice = dto.sellingPrice,
                        imagePath = dto.imagePath,
                        sincronizado = true
                    )
                )
            }
            productDao.obtenerTodos()
        } catch (e: Exception) {
            productDao.obtenerTodos()
        }
    }

    suspend fun agregarProducto(productoDto: ProductoDto): Boolean {
        val entity = ProductEntity(
            remoteId = productoDto.id,
            name = productoDto.name,
            category = productoDto.category,
            stock = productoDto.stock,
            purchasePrice = productoDto.purchasePrice,
            sellingPrice = productoDto.sellingPrice,
            imagePath = productoDto.imagePath,
            sincronizado = false
        )
        productDao.insertar(entity)

        return try {
            val respuesta = cliente.post("$baseUrl/productos") {
                header("apikey", apiKey)
                header("Authorization", "Bearer $apiKey")
                header("Prefer", "return=minimal")
                contentType(ContentType.Application.Json)
                setBody(productoDto)
            }
            respuesta.status.value in 200..299
        } catch (e: Exception) {
            false
        }
    }
}
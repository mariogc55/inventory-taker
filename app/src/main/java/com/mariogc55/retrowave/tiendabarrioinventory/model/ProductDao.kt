package com.mariogc55.retrowave.tiendabarrioinventory.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "productos_locales")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val remoteId: Long? = null,
    val name: String,
    val category: String,
    val stock: Int,
    val purchasePrice: Double,
    val sellingPrice: Double,
    val imagePath: String? = null,
    val sincronizado: Boolean = false
) {
    fun toDto(): ProductoDto = ProductoDto(
        id = remoteId,
        name = name,
        category = category,
        stock = stock,
        purchasePrice = purchasePrice,
        sellingPrice = sellingPrice,
        imagePath = imagePath
    )

    fun toProduct(): Product = Product(
        id = (remoteId ?: localId).toString(),
        name = name,
        category = category,
        stock = stock,
        purchasePrice = purchasePrice,
        sellingPrice = sellingPrice,
        imagePath = imagePath
    )
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM productos_locales")
    suspend fun obtenerTodos(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: ProductEntity): Long

    @Query("DELETE FROM productos_locales")
    suspend fun limpiarTodo()
}
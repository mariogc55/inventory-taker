package com.mariogc55.retrowave.tiendabarrioinventory.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductoDto(
    val id: Long? = null,
    val name: String,
    val category: String,
    val stock: Int,
    @SerialName("purchase_price") val purchasePrice: Double,
    @SerialName("selling_price") val sellingPrice: Double,
    @SerialName("image_path") val imagePath: String? = null
) {
    fun toProduct(): Product = Product(
        id = (id ?: 0L).toString(),
        name = name,
        category = category,
        stock = stock,
        purchasePrice = purchasePrice,
        sellingPrice = sellingPrice,
        imagePath = imagePath
    )
}
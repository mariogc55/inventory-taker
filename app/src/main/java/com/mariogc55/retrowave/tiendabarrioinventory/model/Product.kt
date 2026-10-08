package com.mariogc55.retrowave.tiendabarrioinventory.model

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val stock: Int,
    val purchasePrice: Double,
    val sellingPrice: Double,
    val imagePath: String? = null
) {
    val profitMargin: Double
        get() = if (purchasePrice > 0.0) {
            ((sellingPrice - purchasePrice) / purchasePrice) * 100
        } else {
            0.0
        }
}
package com.mariogc55.retrowave.tiendabarrioinventory.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VentaDto(
    val id: Long? = null,
    @SerialName("product_id") val productId: String? = null,
    @SerialName("product_name") val productName: String,
    val quantity: Int,
    @SerialName("unit_price") val unitPrice: Double,
    @SerialName("total_price") val totalPrice: Double,
    @SerialName("created_at") val createdAt: String? = null
) {
    fun toSale(): Sale = Sale(
        id = (id ?: 0L).toString(),
        productId = productId ?: "",
        productName = productName,
        quantity = quantity,
        unitPrice = unitPrice,
        totalPrice = totalPrice,
        date = createdAt ?: ""
    )

    companion object {
        fun desdeVenta(productId: String, productName: String, quantity: Int, unitPrice: Double): VentaDto =
            VentaDto(
                productId = productId,
                productName = productName,
                quantity = quantity,
                unitPrice = unitPrice,
                totalPrice = quantity * unitPrice
            )
    }
}

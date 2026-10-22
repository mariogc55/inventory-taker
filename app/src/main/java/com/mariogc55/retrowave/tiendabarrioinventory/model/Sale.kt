package com.mariogc55.retrowave.tiendabarrioinventory.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Sale(
    val id: String = System.currentTimeMillis().toString(),
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double = quantity * unitPrice,
    val date: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
)
package com.mariogc55.retrowave.tiendabarrioinventory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mariogc55.retrowave.tiendabarrioinventory.model.Product

private val CardBg = Color(0xFF151C28)
private val TextPrimary = Color(0xFFF1F5F9)
private val TextSecondary = Color(0xFF94A3B8)
private val AccentBlue = Color(0xFF3B82F6)
private val AccentGreen = Color(0xFF10B981)
private val DeleteRedBg = Color(0xFF2D1515)
private val DeleteRedIcon = Color(0xFFEF4444)

@Composable
fun ProductCard(
    product: Product,
    onDeleteClick: () -> Unit
) {
    val productEmoji = when {
        product.name.contains("Coca", ignoreCase = true) -> "🥤"
        product.name.contains("Papas", ignoreCase = true) -> "🍟"
        product.name.contains("Arroz", ignoreCase = true) -> "🍚"
        product.name.contains("Aceite", ignoreCase = true) -> "🛢️"
        product.name.contains("Leche", ignoreCase = true) -> "🥛"
        product.name.contains("Huevos", ignoreCase = true) -> "🥚"
        product.name.contains("Pan", ignoreCase = true) -> "🍞"
        product.name.contains("Azúcar", ignoreCase = true) -> "📦"
        else -> "🛍️"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFF1E293B), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = productEmoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = product.name,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.category,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "•",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+29%",
                        color = AccentGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${product.sellingPrice.toInt()}K",
                        color = AccentBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${product.stock} uds",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(DeleteRedBg, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = DeleteRedIcon,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
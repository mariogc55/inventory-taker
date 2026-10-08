package com.mariogc55.retrowave.tiendabarrioinventory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mariogc55.retrowave.tiendabarrioinventory.model.Product

private val CardBg = Color(0xFF181F2C)
private val PrimaryBlue = Color(0xFF3B82F6)

@Composable
fun ProductCard(
    product: Product,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF222B3D)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    color = Color.White,
                    fontSize = 15.sp,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = product.category, color = Color.Gray, fontSize = 12.sp)
                    Text(text = " • ", color = Color.Gray, fontSize = 12.sp)
                    Text(text = "+${product.profitMargin.toInt()}%", color = Color(0xFF34D399), fontSize = 12.sp)
                    Text(text = " • ", color = Color.Gray, fontSize = 12.sp)
                    Text(text = "${product.stock} uds", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${(product.sellingPrice / 1000).toInt()}K",
                    color = PrimaryBlue,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color(0xFF2D1B20), shape = RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
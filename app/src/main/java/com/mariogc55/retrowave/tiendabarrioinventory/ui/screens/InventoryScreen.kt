package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mariogc55.retrowave.tiendabarrioinventory.model.Product
import com.mariogc55.retrowave.tiendabarrioinventory.ui.components.ProductCard

@Composable
fun InventoryScreen() {
    val sampleProducts = listOf(
        Product(1, "Arroz Diana 1kg", 25, 4.50),
        Product(2, "Aceite Girasol 1L", 12, 9.20),
        Product(3, "Azúcar Morena 1kg", 30, 3.80),
        Product(4, "Leche Entera 1L", 15, 2.50)
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Inventario de la Tienda",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sampleProducts) { product ->
                    ProductCard(product = product)
                }
            }
        }
    }
}
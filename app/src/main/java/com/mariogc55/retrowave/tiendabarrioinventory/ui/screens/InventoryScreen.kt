package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mariogc55.retrowave.tiendabarrioinventory.R
import com.mariogc55.retrowave.tiendabarrioinventory.model.Product
import com.mariogc55.retrowave.tiendabarrioinventory.ui.components.ProductCard

private val ScreenDarkBg = Color(0xFF0F141C)
private val ScreenCardBg = Color(0xFF181F2C)
private val ScreenPrimaryBlue = Color(0xFF3B82F6)

@Composable
fun InventoryScreen() {
    var searchQuery by remember { mutableStateOf("") }

    val sampleProducts = listOf(
        Product(id = "1", name = "Coca-Cola 600ml", category = "Bebidas", stock = 24, purchasePrice = 9000.0, sellingPrice = 12000.0),
        Product(id = "2", name = "Papas Margarita 100g", category = "Snacks", stock = 18, purchasePrice = 3500.0, sellingPrice = 5000.0),
        Product(id = "3", name = "Arroz Diana 1kg", category = "Granos", stock = 30, purchasePrice = 5500.0, sellingPrice = 7000.0)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.inventory_taker_logo),
                    contentDescription = "Inventory Taker Logo",
                    modifier = Modifier.size(150.dp, 45.dp),
                    contentScale = ContentScale.Fit
                )

                Button(
                    onClick = { /* Acción escanear */ },
                    colors = ButtonDefaults.buttonColors(containerColor = ScreenCardBg),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = R.drawable.icono_escanear_producto), contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Escanear", color = ScreenPrimaryBlue, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Inventario",
                fontSize = 24.sp,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "${sampleProducts.size} productos",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar producto, categoría, código...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ScreenPrimaryBlue,
                    unfocusedBorderColor = Color(0xFF2D3748),
                    focusedContainerColor = ScreenCardBg,
                    unfocusedContainerColor = ScreenCardBg,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(sampleProducts) { product ->
                    ProductCard(
                        product = product,
                        onDeleteClick = { /* Acción para eliminar producto (RF-02) */ }
                    )
                }
            }
        }

        Button(
            onClick = { /* Abrir diálogo o pantalla de nuevo producto */ },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .fillMaxWidth(0.85f)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ScreenPrimaryBlue)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Agregar Producto", color = Color.White, fontSize = 16.sp)
        }
    }
}
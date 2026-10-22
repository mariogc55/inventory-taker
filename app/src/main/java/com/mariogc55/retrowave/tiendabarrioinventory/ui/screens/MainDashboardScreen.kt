package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mariogc55.retrowave.tiendabarrioinventory.model.Product
import com.mariogc55.retrowave.tiendabarrioinventory.model.Sale

val DarkBackground = Color(0xFF0B0F17)
val CardBackground = Color(0xFF151C28)
val PrimaryBlue = Color(0xFF3B82F6)

@Composable
fun MainDashboardScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Inventario", "Ventas", "Gastos", "Ganancias", "Reportes")

    val sharedProducts = remember {
        mutableStateListOf(
            Product(id = "1", name = "Coca-Cola 600ml", category = "Bebidas", stock = 24, purchasePrice = 9000.0, sellingPrice = 12000.0),
            Product(id = "2", name = "Papas Margarita 100g", category = "Snacks", stock = 18, purchasePrice = 3500.0, sellingPrice = 5000.0),
            Product(id = "3", name = "Arroz Diana 1kg", category = "Granos", stock = 30, purchasePrice = 5500.0, sellingPrice = 7000.0),
            Product(id = "4", name = "Aceite Girasol 1L", category = "Abarrotes", stock = 12, purchasePrice = 12000.0, sellingPrice = 16000.0),
            Product(id = "5", name = "Leche Lala 1L", category = "Lácteos", stock = 15, purchasePrice = 6000.0, sellingPrice = 8000.0),
            Product(id = "6", name = "Huevos x12", category = "Lácteos", stock = 20, purchasePrice = 10000.0, sellingPrice = 14000.0)
        )
    }

    val sharedSales = remember {
        mutableStateListOf(
            Sale(productId = "3", productName = "Arroz Diana 1kg", quantity = 1, unitPrice = 7000.0),
            Sale(productId = "6", productName = "Huevos x12", quantity = 4, unitPrice = 13500.0)
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0B0F17),
                contentColor = Color.White
            ) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = {
                            when (index) {
                                0 -> Icon(Icons.Default.Home, contentDescription = title)
                                1 -> Icon(Icons.Default.ShoppingCart, contentDescription = title)
                                2 -> Icon(Icons.Default.Menu, contentDescription = title)
                                3 -> Icon(Icons.Default.Star, contentDescription = title)
                                4 -> Icon(Icons.Default.DateRange, contentDescription = title)
                            }
                        },
                        label = {
                            Text(
                                title,
                                color = if (selectedTab == index) PrimaryBlue else Color.Gray
                            )
                        },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = PrimaryBlue,
                            unselectedIconColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> InventoryScreen()
                1 -> VentasScreen(availableProducts = sharedProducts, salesHistory = sharedSales)
                2 -> GastosScreen()
                3 -> GananciasScreen()
                4 -> ReportesScreen()
            }
        }
    }
}
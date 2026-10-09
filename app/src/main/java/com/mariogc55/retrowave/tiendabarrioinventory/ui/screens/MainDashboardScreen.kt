package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val DarkBackground = Color(0xFF0F141C)
val CardBackground = Color(0xFF181F2C)
val PrimaryBlue = Color(0xFF3B82F6)

@Composable
fun MainDashboardScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Inventario", "Ventas", "Gastos", "Ganancias", "Reportes")

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
                                2 -> Icon(Icons.Default.List, contentDescription = title)
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
                1 -> VentasScreenPlaceholder()
                2 -> GastosScreen()
                3 -> GananciasScreen()
                4 -> ReportesScreen()
            }
        }
    }
}

@Composable
fun VentasScreenPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Pantalla de Ventas", color = Color.White)
    }
}
package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

val DarkBackground = Color(0xFF0F141C)
val CardBackground = Color(0xFF181F2C)
val PrimaryBlue = Color(0xFF3B82F6)

@Composable
fun MainDashboardScreen() {
    var selectedTab

            by remember { mutableStateOf(0) }
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
                                2 -> Icon(Icons.Default.Send, contentDescription = title)
                                3 -> Icon(Icons.Default.Star, contentDescription = title)
                                4 -> Icon(Icons.Default.List, contentDescription = title)
                            }
                        },
                        label = { Text(title, color = if (selectedTab == index) PrimaryBlue else Color.Gray) },
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
                2 -> GastosScreenPlaceholder()
                3 -> GananciasScreenPlaceholder()
                4 -> ReportesScreenPlaceholder()
            }
        }
    }
}

@Composable
fun VentasScreenPlaceholder() {
    Box(modifier = Modifier.fillMaxSize().background(DarkBackground), contentAlignment = androidx.compose.ui.Alignment.Center) {
        Text("Pantalla de Ventas", color = Color.White)
    }
}

@Composable
fun GastosScreenPlaceholder() {
    Box(modifier = Modifier.fillMaxSize().background(DarkBackground), contentAlignment = androidx.compose.ui.Alignment.Center) {
        Text("Pantalla de Gastos", color = Color.White)
    }
}

@Composable
fun GananciasScreenPlaceholder() {
    Box(modifier = Modifier.fillMaxSize().background(DarkBackground), contentAlignment = androidx.compose.ui.Alignment.Center) {
        Text("Pantalla de Ganancias", color = Color.White)
    }
}

@Composable
fun ReportesScreenPlaceholder() {
    Box(modifier = Modifier.fillMaxSize().background(DarkBackground), contentAlignment = androidx.compose.ui.Alignment.Center) {
        Text("Pantalla de Reportes", color = Color.White)
    }
}
package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mariogc55.retrowave.tiendabarrioinventory.R
import com.mariogc55.retrowave.tiendabarrioinventory.model.Product
import com.mariogc55.retrowave.tiendabarrioinventory.ui.components.ProductCard
import java.text.Normalizer

private val ScreenDarkBg = Color(0xFF0B0F17)
private val ScreenCardBg = Color(0xFF151C28)
private val ScreenPrimaryBlue = Color(0xFF3B82F6)
private val ScreenTextColor = Color(0xFFF1F5F9)

fun String.normalizeSearch(): String {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    return Regex("\\p{InCombiningDiacriticalMarks}+").replace(normalized, "").lowercase().trim()
}

@Composable
fun InventoryScreen() {
    var searchQuery by remember { mutableStateOf("") }

    val productsList = remember {
        mutableStateListOf(
            Product(id = "1", name = "Coca-Cola 600ml", category = "Bebidas", stock = 24, purchasePrice = 9000.0, sellingPrice = 12000.0),
            Product(id = "2", name = "Papas Margarita 100g", category = "Snacks", stock = 18, purchasePrice = 3500.0, sellingPrice = 5000.0),
            Product(id = "3", name = "Arroz Diana 1kg", category = "Granos", stock = 30, purchasePrice = 5500.0, sellingPrice = 7000.0),
            Product(id = "4", name = "Aceite Girasol 1L", category = "Abarrotes", stock = 12, purchasePrice = 12000.0, sellingPrice = 16000.0),
            Product(id = "5", name = "Leche Lala 1L", category = "Lácteos", stock = 15, purchasePrice = 6000.0, sellingPrice = 8000.0),
            Product(id = "6", name = "Huevos x12", category = "Lácteos", stock = 20, purchasePrice = 10000.0, sellingPrice = 14000.0)
        )
    }

    var productToDelete by remember { mutableStateOf<Product?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredProducts = remember(searchQuery, productsList.toList()) {
        if (searchQuery.isBlank()) {
            productsList
        } else {
            val query = searchQuery.normalizeSearch()
            productsList.filter { product ->
                product.name.normalizeSearch().contains(query) ||
                        product.category.normalizeSearch().contains(query) ||
                        product.id.normalizeSearch().contains(query)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.inventory_taker_logo),
                    contentDescription = "Inventory Taker Logo",
                    modifier = Modifier.size(140.dp, 40.dp),
                    contentScale = ContentScale.Fit
                )

                Surface(
                    onClick = { /* Acción escanear */ },
                    color = ScreenCardBg,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icono_escanear_producto),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Escanear",
                            color = ScreenPrimaryBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Inventario",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ScreenTextColor
            )
            Text(
                text = "${filteredProducts.size} productos",
                fontSize = 14.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar producto, categoría, código...", color = Color(0xFF64748B), fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ScreenPrimaryBlue,
                    unfocusedBorderColor = Color(0xFF1E293B),
                    focusedContainerColor = ScreenCardBg,
                    unfocusedContainerColor = ScreenCardBg,
                    focusedTextColor = ScreenTextColor,
                    unfocusedTextColor = ScreenTextColor
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotBlank()) "No se encontraron productos con ese criterio" else "No hay productos registrados",
                        color = Color(0xFF64748B),
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onDeleteClick = { productToDelete = product }
                        )
                    }
                }
            }
        }

        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .fillMaxWidth(0.88f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ScreenPrimaryBlue)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Agregar Producto", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }

    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Eliminar Producto", color = ScreenTextColor) },
            text = { Text("¿Está seguro de eliminar '${product.name}' del inventario?", color = Color(0xFF94A3B8)) },
            containerColor = ScreenCardBg,
            confirmButton = {
                TextButton(
                    onClick = {
                        productsList.remove(product)
                        productToDelete = null
                    }
                ) {
                    Text("Eliminar", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancelar", color = Color(0xFF64748B))
                }
            }
        )
    }

    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onAddProduct = { newProduct ->
                productsList.add(newProduct)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAddProduct: (Product) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var stockText by remember { mutableStateOf("") }
    var purchasePriceText by remember { mutableStateOf("") }
    var sellingPriceText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Producto", color = ScreenTextColor) },
        containerColor = ScreenCardBg,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = Color(0xFFEF4444), fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del producto") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = ScreenTextColor, unfocusedTextColor = ScreenTextColor)
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoría (ej. Bebidas, Snacks)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = ScreenTextColor, unfocusedTextColor = ScreenTextColor)
                )

                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = { Text("Stock inicial") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = ScreenTextColor, unfocusedTextColor = ScreenTextColor)
                )

                OutlinedTextField(
                    value = purchasePriceText,
                    onValueChange = { purchasePriceText = it },
                    label = { Text("Precio de compra ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = ScreenTextColor, unfocusedTextColor = ScreenTextColor)
                )

                OutlinedTextField(
                    value = sellingPriceText,
                    onValueChange = { sellingPriceText = it },
                    label = { Text("Precio de venta ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = ScreenTextColor, unfocusedTextColor = ScreenTextColor)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val purchase = purchasePriceText.toDoubleOrNull() ?: 0.0
                    val selling = sellingPriceText.toDoubleOrNull() ?: 0.0
                    val stock = stockText.toIntOrNull() ?: -1

                    when {
                        name.isBlank() -> errorMessage = "El nombre es obligatorio"
                        category.isBlank() -> errorMessage = "Seleccione una categoría"
                        purchase <= 0 -> errorMessage = "El precio de compra debe ser mayor a 0"
                        selling < purchase -> errorMessage = "El precio de venta debe ser ≥ al de compra"
                        stock < 0 -> errorMessage = "El stock debe ser un número entero ≥ 0"
                        else -> {
                            onAddProduct(
                                Product(
                                    id = System.currentTimeMillis().toString(),
                                    name = name,
                                    category = category,
                                    stock = stock,
                                    purchasePrice = purchase,
                                    sellingPrice = selling
                                )
                            )
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ScreenPrimaryBlue)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF64748B))
            }
        }
    )
}
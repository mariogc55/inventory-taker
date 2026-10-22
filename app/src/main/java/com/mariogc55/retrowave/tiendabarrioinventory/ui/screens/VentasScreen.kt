package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mariogc55.retrowave.tiendabarrioinventory.R
import com.mariogc55.retrowave.tiendabarrioinventory.model.Product
import com.mariogc55.retrowave.tiendabarrioinventory.model.Sale

private val ScreenDarkBg = Color(0xFF0B0F17)
private val ScreenCardBg = Color(0xFF151C28)
private val ScreenPrimaryBlue = Color(0xFF3B82F6)
private val ScreenTextColor = Color(0xFFF1F5F9)
private val ScreenGreenEmerald = Color(0xFF10B981)

fun Double.toFigmaCurrency(): String {
    return if (this >= 1000) {
        "$${(this / 1000).toInt()}K"
    } else {
        "$${this.toInt()}"
    }
}

@Composable
fun VentasScreen(
    availableProducts: MutableList<Product>,
    salesHistory: MutableList<Sale>
) {
    var activeTab by remember { mutableStateOf("Historial") }
    var searchQuery by remember { mutableStateOf("") }
    var showScanModal by remember { mutableStateOf(false) }
    var showRegisterModal by remember { mutableStateOf(false) }
    var preselectedProduct by remember { mutableStateOf<Product?>(null) }
    var alertMessage by remember { mutableStateOf<String?>(null) }

    val filteredProducts = remember(searchQuery, availableProducts.toList()) {
        if (searchQuery.isBlank()) availableProducts
        else availableProducts.filter { it.name.lowercase().contains(searchQuery.lowercase().trim()) }
    }

    val filteredSales = remember(searchQuery, salesHistory.toList()) {
        if (searchQuery.isBlank()) salesHistory
        else salesHistory.filter { it.productName.lowercase().contains(searchQuery.lowercase().trim()) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.inventory_taker_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(130.dp, 36.dp),
                    contentScale = ContentScale.Fit
                )

                Surface(
                    onClick = { showScanModal = true },
                    color = ScreenCardBg,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icono_escanear_producto),
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Escanear", color = ScreenPrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Ventas", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ScreenTextColor)
            Text("Oct 2026", fontSize = 13.sp, color = Color(0xFF94A3B8))

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val totalHoy = salesHistory.sumOf { it.totalPrice }
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "HOY",
                    value = totalHoy.toFigmaCurrency(),
                    subtext = "${salesHistory.size} ventas",
                    valueColor = ScreenGreenEmerald
                )
                MetricCard(modifier = Modifier.weight(1f), title = "ESTE MES", value = "$1.0M", subtext = "45 ventas", valueColor = ScreenGreenEmerald)
                MetricCard(modifier = Modifier.weight(1f), title = "MES ANT.", value = "$802K", subtext = "↑ 29%", valueColor = ScreenTextColor, badgeColor = ScreenGreenEmerald)
            }

            Spacer(modifier = Modifier.height(10.dp))

            alertMessage?.let { msg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF065F46)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(text = msg, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ScreenCardBg, RoundedCornerShape(10.dp))
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (activeTab == "Historial") ScreenDarkBg else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { activeTab = "Historial" }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Historial",
                        color = if (activeTab == "Historial") ScreenTextColor else Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (activeTab == "Vender") ScreenDarkBg else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { activeTab = "Vender" }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Vender",
                        color = if (activeTab == "Vender") ScreenTextColor else Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        if (activeTab == "Historial") "Buscar venta..." else "Buscar producto...",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ScreenPrimaryBlue,
                    unfocusedBorderColor = Color(0xFF1E293B),
                    focusedContainerColor = ScreenCardBg,
                    unfocusedContainerColor = ScreenCardBg,
                    focusedTextColor = ScreenTextColor,
                    unfocusedTextColor = ScreenTextColor
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 85.dp)
            ) {
                if (activeTab == "Historial") {
                    items(filteredSales) { sale ->
                        FigmaSaleCard(sale = sale)
                    }
                } else {
                    items(filteredProducts) { product ->
                        FigmaProductSaleItem(
                            product = product,
                            onItemClick = {
                                preselectedProduct = product
                                showRegisterModal = true
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                preselectedProduct = null
                showRegisterModal = true
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .fillMaxWidth(0.88f)
                .height(46.dp),
            shape = RoundedCornerShape(23.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ScreenPrimaryBlue)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Registrar Venta", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }

    if (showScanModal) {
        ScanCodeBottomSheet(
            products = availableProducts,
            onDismiss = { showScanModal = false },
            onProductScanned = { scannedProd ->
                showScanModal = false
                preselectedProduct = scannedProd
                showRegisterModal = true
            },
            onError = { err -> alertMessage = err }
        )
    }

    if (showRegisterModal) {
        RegisterSaleBottomSheet(
            products = availableProducts,
            initialProduct = preselectedProduct,
            onDismiss = { showRegisterModal = false },
            onConfirmSale = { selectedProduct, quantity ->
                val index = availableProducts.indexOfFirst { it.id == selectedProduct.id }
                if (index != -1) {
                    availableProducts[index] = selectedProduct.copy(stock = selectedProduct.stock - quantity)
                }
                salesHistory.add(
                    0,
                    Sale(
                        productId = selectedProduct.id,
                        productName = selectedProduct.name,
                        quantity = quantity,
                        unitPrice = selectedProduct.sellingPrice
                    )
                )
                alertMessage = "✓ Venta registrada exitosamente"
                showRegisterModal = false
            }
        )
    }
}

@Composable
fun MetricCard(
    modifier: Modifier,
    title: String,
    value: String,
    subtext: String,
    valueColor: Color,
    badgeColor: Color? = null
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = ScreenCardBg),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(title, fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = valueColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtext, fontSize = 10.sp, color = badgeColor ?: Color(0xFF64748B))
        }
    }
}

@Composable
fun FigmaSaleCard(sale: Sale) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ScreenCardBg),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF0F2922), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = ScreenGreenEmerald,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(sale.productName, color = ScreenTextColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${sale.quantity} × ${sale.unitPrice.toFigmaCurrency()} • 27 de oct, 06:44 p. m.", color = Color(0xFF94A3B8), fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(sale.totalPrice.toFigmaCurrency(), color = ScreenGreenEmerald, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("+${(sale.totalPrice * 0.25).toFigmaCurrency()}", color = Color(0xFF64748B), fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun FigmaProductSaleItem(product: Product, onItemClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ScreenCardBg),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF1E293B), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🥤", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, color = ScreenTextColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Stock: ${product.stock}", color = Color(0xFF94A3B8), fontSize = 11.sp)
            }
            Text(product.sellingPrice.toFigmaCurrency(), color = ScreenGreenEmerald, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanCodeBottomSheet(
    products: List<Product>,
    onDismiss: () -> Unit,
    onProductScanned: (Product) -> Unit,
    onError: (String) -> Unit
) {
    var manualCodeInput by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ScreenCardBg,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Escanear Código QR", color = ScreenTextColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(220.dp)
                    .background(Color.Black, RoundedCornerShape(14.dp))
                    .border(2.dp, ScreenGreenEmerald, RoundedCornerShape(14.dp))
                    .clickable {
                        val productFound = products.firstOrNull { it.stock > 0 }
                        if (productFound != null) {
                            onProductScanned(productFound)
                        } else {
                            onError("No hay productos con stock en inventario")
                            onDismiss()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = ScreenGreenEmerald, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Toca para simular escaneo", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("— o ingresa el código manualmente —", color = Color(0xFF64748B), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = manualCodeInput,
                    onValueChange = { manualCodeInput = it },
                    placeholder = { Text("Código de barras o QR", color = Color(0xFF64748B), fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ScreenDarkBg,
                        unfocusedContainerColor = ScreenDarkBg,
                        focusedTextColor = ScreenTextColor,
                        unfocusedTextColor = ScreenTextColor
                    )
                )
                Button(
                    onClick = {
                        val query = manualCodeInput.lowercase().trim()
                        val found = products.find { it.id == query || it.name.lowercase().contains(query) }
                        if (found != null) {
                            onProductScanned(found)
                        } else {
                            onError("X Producto no encontrado en inventario")
                            onDismiss()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ScreenPrimaryBlue)
                ) {
                    Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterSaleBottomSheet(
    products: List<Product>,
    initialProduct: Product? = null,
    onDismiss: () -> Unit,
    onConfirmSale: (Product, Int) -> Unit
) {
    var selectedProduct by remember { mutableStateOf(initialProduct) }
    var quantity by remember { mutableStateOf(1) }
    var expandedDropdown by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ScreenCardBg,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Registrar Venta", color = ScreenTextColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = Color(0xFFEF4444), fontSize = 12.sp)
            }

            ExposedDropdownMenuBox(
                expanded = expandedDropdown,
                onExpandedChange = { expandedDropdown = !expandedDropdown }
            ) {
                OutlinedTextField(
                    value = selectedProduct?.let { "${it.name} (stock: ${it.stock})" } ?: "Selecciona un producto",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ScreenDarkBg,
                        unfocusedContainerColor = ScreenDarkBg,
                        focusedTextColor = ScreenTextColor,
                        unfocusedTextColor = ScreenTextColor
                    )
                )

                ExposedDropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false },
                    modifier = Modifier.background(ScreenDarkBg)
                ) {
                    products.forEach { product ->
                        DropdownMenuItem(
                            text = { Text("${product.name} (stock: ${product.stock})", color = ScreenTextColor) },
                            onClick = {
                                selectedProduct = product
                                expandedDropdown = false
                                errorMessage = null
                            }
                        )
                    }
                }
            }

            Text("Cantidad", color = Color(0xFF94A3B8), fontSize = 11.sp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { if (quantity > 1) quantity-- },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ScreenDarkBg)
                ) {
                    Text("-", color = ScreenTextColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .background(ScreenDarkBg, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$quantity", color = ScreenTextColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { quantity++ },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ScreenDarkBg)
                ) {
                    Text("+", color = ScreenTextColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    val product = selectedProduct
                    when {
                        product == null -> errorMessage = "Selecciona un producto"
                        quantity < 1 -> errorMessage = "La cantidad debe ser al menos 1"
                        product.stock < quantity -> errorMessage = "Stock insuficiente para la cantidad solicitada"
                        else -> onConfirmSale(product, quantity)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ScreenPrimaryBlue)
            ) {
                Text("Confirmar Venta", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ScreenDarkBg)
            ) {
                Text("Cancelar", color = ScreenTextColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mariogc55.retrowave.tiendabarrioinventory.model.Gasto

@Composable
fun GastosScreen() {
    // Gastos de ejemplo del mes (RF-08 los registra, aquí solo los mostramos agrupados)
    val gastosDelMes = listOf(
        Gasto(1, "Arriendo local", 800000.0, "Arriendo"),
        Gasto(2, "Factura de luz", 120000.0, "Servicios"),
        Gasto(3, "Factura de agua", 45000.0, "Servicios"),
        Gasto(4, "Compra de bebidas", 350000.0, "Compras"),
        Gasto(5, "Compra de snacks", 200000.0, "Compras"),
        Gasto(6, "Bolsas plásticas", 30000.0, "Insumos")
    )

    // RF-09: agrupar gastos por categoría y sumar el monto de cada una
    val gastosAgrupados: Map<String, Double> = gastosDelMes
        .groupBy { it.categoria }
        .mapValues { entrada -> entrada.value.sumOf { it.monto } }

    // Total de gastos del mes (para calcular el porcentaje)
    val totalGastosMes = gastosAgrupados.values.sum()

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
                text = "Gastos por Categoría",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total del mes: $${"%,.0f".format(totalGastosMes)}",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(gastosAgrupados.entries.toList()) { entrada ->
                    val categoria = entrada.key
                    val montoCategoria = entrada.value

                    // Fórmula RF-09: porcentaje = (gasto_categoría / total_gastos_mes) × 100
                    // Si total = 0, mostrar 0% sin error
                    val porcentaje = if (totalGastosMes > 0) {
                        (montoCategoria / totalGastosMes) * 100
                    } else {
                        0.0
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = categoria,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$${"%,.0f".format(montoCategoria)}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${"%.1f".format(porcentaje)}%",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
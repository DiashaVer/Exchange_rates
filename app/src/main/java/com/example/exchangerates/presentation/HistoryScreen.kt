package com.example.exchangerates.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    currencyId: String,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val history by viewModel.history.collectAsState()
    val allCurrencies by viewModel.allCurrencies.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru")) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        TextButton(onClick = { expanded = true }, modifier = Modifier.menuAnchor()) {
                            Text(selectedCurrency?.code ?: "Выберите валюту")
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            allCurrencies.forEach { currency ->
                                DropdownMenuItem(
                                    text = { Text("${currency.code} - ${String.format(Locale.getDefault(), "%.2f", currency.rate)}") },
                                    onClick = {
                                        viewModel.selectCurrency(currency)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (history.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Динамика курса", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        LineChart(
                            data = history.map { it.rate },
                            lineColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            if (history.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет исторических данных")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(history.size) { index ->
                        val entry = history[index]
                        val previousRate = history.getOrNull(index + 1)?.rate ?: entry.rate
                        val trend = entry.rate - previousRate
                        HistoryCard(
                            date = entry.date.format(dateFormatter),
                            rate = entry.rate,
                            trend = trend
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(date: String, rate: Double, trend: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = date, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                Text(text = String.format(Locale.getDefault(), "%.2f ₽", rate), style = MaterialTheme.typography.titleLarge)
            }
            val color = when {
                trend > 0 -> Color(0xFF4CAF50)
                trend < 0 -> Color(0xFFF44336)
                else -> MaterialTheme.colorScheme.outline
            }
            val icon = when {
                trend > 0 -> Icons.Default.TrendingUp
                trend < 0 -> Icons.Default.TrendingDown
                else -> Icons.Default.TrendingFlat
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (trend != 0.0) {
                    Text(text = String.format(Locale.getDefault(), "%.2f", trend), color = color, style = MaterialTheme.typography.labelLarge)
                }
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.padding(start = 4.dp).size(20.dp))
            }
        }
    }
}
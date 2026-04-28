package com.example.exchangerates.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController, //  для кнопки назад
    currencyCode: String,
    viewModel: HistoryViewModel = viewModel(factory = HistoryViewModelFactory(currencyCode)) //посреденик конкретной валюты кода из навигации
) {
    val history by viewModel.historyRates.collectAsState() //поток данных Flow
    val dateFormatter = remember { DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru")) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("История $currencyCode") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (history.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator() // Покажем загрузку, если данных нет
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Используем itemsIndexed, чтобы сравнивать текущий курс с предыдущим
                itemsIndexed(history) { index, (date, rate) ->
                    // Вычисление графиков (сравнение со вчерашним днем)
                    val previousRate = history.getOrNull(index + 1)?.second ?: rate
                    val trend = rate - previousRate

                    HistoryCard(
                        date = date.format(dateFormatter),
                        rate = rate,
                        trend = trend
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryCard(date: String, rate: Double, trend: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = String.format(Locale.getDefault(), "%.2f ₽", rate),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // Блок отображения изменения курса графики
            val color = when {
                trend > 0 -> Color(0xFF4CAF50) // Зеленый
                trend < 0 -> Color(0xFFF44336) // Красный
                else -> MaterialTheme.colorScheme.outline
            }

            val icon = when {
                trend > 0 -> Icons.Default.TrendingUp
                trend < 0 -> Icons.Default.TrendingDown
                else -> Icons.Default.TrendingFlat
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (trend != 0.0) {
                    Text(
                        text = String.format(Locale.getDefault(), "%.2f", trend),
                        color = color,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.padding(start = 4.dp).size(20.dp)
                )
            }
        }
    }
}

package com.example.exchangerates.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.util.Locale

/*
    Часть 5.2 – Поддержка отображения нескольких валют одновременно для сравнения.
    Пользователь выбирает валюты чекбоксами, приложение показывает их графики динамики.

    Часть 5.1 – Графики исторических курсов (LineChart) для выбранных валют.

    Часть 4.2 – MVVM: экран получает ComparisonViewModel через Hilt.
    Часть 4.3 – DI (Hilt) через hiltViewModel().

    Часть 3.3 – Отображение загруженных исторических данных в интерфейсе.
    Часть 2.1 – Использует исторические курсы выбранных валют (через ViewModel).

    Часть 5.4 – Базовые анимации могут быть добавлены (здесь используются стандартные карточки,
    но при обновлении данных Compose автоматически применяет анимации списка).
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonScreen(
    navController: NavController,
    viewModel: ComparisonViewModel = hiltViewModel()
) {
    val allCurrencies by viewModel.allCurrencies.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val historyData by viewModel.historyData.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Сравнение валют") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Выберите валюты для сравнения", style = MaterialTheme.typography.titleMedium)
                        // Отображение чекбоксов для выбора нескольких валют (часть 5, пункт 2)
                        allCurrencies.forEach { currency ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = selectedIds.contains(currency.id),
                                    onCheckedChange = { viewModel.toggleCurrency(currency.id) }
                                )
                                Text(
                                    "${currency.code} - ${String.format(Locale.getDefault(), "%.2f", currency.rate)}",
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (selectedIds.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Графики и динамика", style = MaterialTheme.typography.titleMedium)
                            // Для каждой выбранной валюты отображаем график исторических курсов
                            selectedIds.forEach { currencyId ->
                                val currency = allCurrencies.find { it.id == currencyId }
                                val history = historyData[currencyId] ?: emptyList()
                                if (history.isNotEmpty()) {
                                    Text(
                                        currency?.code ?: currencyId,
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                    // Часть 5.1 – простой график LineChart
                                    LineChart(
                                        data = history.map { it.rate },
                                        modifier = Modifier.height(150.dp),
                                        lineColor = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Text("Нет данных для ${currency?.code ?: currencyId}")
                                }
                                Divider()
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Выберите хотя бы одну валюту")
                        }
                    }
                }
            }
        }
    }
}
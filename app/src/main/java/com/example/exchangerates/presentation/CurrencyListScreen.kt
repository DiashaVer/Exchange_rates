package com.example.exchangerates.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.exchangerates.domain.model.Currency
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyListScreen(
    navController: NavController,
    viewModel: CurrencyListViewModel
) {
    val sortedCurrencies = viewModel.getSortedCurrencies() //отсортированный список избранного
    //CRUD показ диалоговых окон
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCurrency by remember { mutableStateOf<Currency?>(null) }
    var currencyToDelete by remember { mutableStateOf<Currency?>(null) }

    val snackbarHostState = remember { SnackbarHostState() } //часть 2.6 отображение SnackBar
    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold( //шаблон шапки экрана
        topBar = { CenterAlignedTopAppBar(title = { Text("Курсы валют") }) },
        floatingActionButton = { //кнопка +
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить") // часть 1.2 реализация кнопки добавить валюту
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) } //контейнер для отображ. внизу экрана информац.
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (sortedCurrencies.isEmpty()) {
                Text("Нет валют, нажмите +", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyVerticalGrid( //часть 1.3 LazyVerticalGrid
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sortedCurrencies, key = { it.id }) { currency ->
                        CurrencyCard( // часть 1.4 Для отображения каждой валюты Card
                            currency = currency,
                            onFavorite = { viewModel.toggleFavorite(currency) },
                            onEdit = { editingCurrency = currency },
                            onDelete = { currencyToDelete = currency },
                            onHistory = { navController.navigate("history/${currency.code}") }
                        )
                    }
                }
            }
        }
    }

    // часть 1.7 Ввод данных реализовать через диалоговое окно
    if (showAddDialog) {
        CurrencyDialog(
            title = "Добавить валюту",
            onConfirm = { code, _ ->
                viewModel.addCurrency(code, null)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    editingCurrency?.let { currency ->
        CurrencyDialog(
            title = "Редактировать ${currency.code}",
            initialCode = currency.code,
            onConfirm = { newCode, _ ->
                viewModel.updateCurrency(currency, newCode, null)
                editingCurrency = null
            },
            onDismiss = { editingCurrency = null }
        )
    }

    currencyToDelete?.let { currency ->
        AlertDialog(
            onDismissRequest = { currencyToDelete = null },
            title = { Text("Удаление") },
            text = { Text("Удалить валюту ${currency.code}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCurrency(currency)
                    currencyToDelete = null
                }) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { currencyToDelete = null }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun CurrencyCard( //часть 1.4-1.5 визуальное предствлание одной карточки валюты
    currency: Currency,
    onFavorite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onHistory: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.medium //закругление углов
    ) {
        Box(modifier = Modifier.fillMaxWidth()) { //поверх слоя
            // Крестик удаления (правый угол)
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(24.dp)
            ) {
                Icon( //иконка крестика
                    Icons.Default.Close,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.4f), //полупрозарчность цвета
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(end = 20.dp) //отступ чтобы крестик не накладывался на звезду
                ) {
                    Text(text = getFlagEmoji(currency.code), style = MaterialTheme.typography.titleLarge) // часть 1.5 реализация
                    Spacer(modifier = Modifier.width(6.dp)) //чтобы флаг и текс не слиплись
                    Text(
                        text = currency.code,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f) //чтобы занять все свободеное место
                    )

                    IconButton(onClick = onFavorite, modifier = Modifier.size(32.dp)) { // часть 2.5 реализация избранного
                        Icon(
                            if (currency.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Избранное",
                            tint = if (currency.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, //смена цвета звезды
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // часть 1.5 реализация отображения курса
                Text(
                    text = "Курс: ${String.format(Locale.getDefault(), "%.2f", currency.rate)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // темно-серый цвет
                )

                // расположение кнопки управления внизу карточек история и редактирование
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button( //переход к истории
                        onClick = onHistory,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("История", maxLines = 1)
                    }

                    OutlinedIconButton( //редактирование
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Правка", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyDialog( //часть 1.7 Ввод данных реализация
    title: String,
    initialCode: String = "",
    onConfirm: (code: String, iconRes: Int?) -> Unit,
    onDismiss: () -> Unit
) {
    var code by remember { mutableStateOf(initialCode) }   //текст пользователя
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField( // поле для ввода данных
                value = code,
                onValueChange = { if (it.length <= 3) code = it },
                label = { Text("Код валюты (3 буквы)") },
                singleLine = true
            )
        },
        confirmButton = { //кнопка подтверждения
            TextButton(onClick = {
                if (code.length == 3) onConfirm(code.uppercase(), null)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

fun getFlagEmoji(currencyCode: String): String { //часть 1.5 иконки флаги валюты автоматически
    if (currencyCode.length < 2) return "💰"
    val countryCode = when(currencyCode.uppercase()) { //сопоставление валюты и страны
        "EUR" -> "EU"
        "USD" -> "US"
        "RUB" -> "RU"
        "GBP" -> "GB"
        "JPY" -> "JP"
        "CNY" -> "CN"
        else -> currencyCode.substring(0, 2).uppercase()
    }
    return try {
        val firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
        String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    } catch (e: Exception) {
        "💰"
    }
}

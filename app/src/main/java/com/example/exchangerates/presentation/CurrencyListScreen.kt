package com.example.exchangerates.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
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
    val currencies by viewModel.currencies.collectAsState()
    val filterText by viewModel.filterText.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingCurrency by remember { mutableStateOf<Currency?>(null) }
    var currencyToDelete by remember { mutableStateOf<Currency?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Курсы валют") },
                actions = {
                    // Кнопка сортировки
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Сортировка")
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("По коду") },
                            onClick = {
                                viewModel.updateSortType(SortType.BY_CODE)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("По курсу") },
                            onClick = {
                                viewModel.updateSortType(SortType.BY_RATE)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("По избранному") },
                            onClick = {
                                viewModel.updateSortType(SortType.BY_FAVORITE)
                                showSortMenu = false
                            }
                        )
                    }
                    // Кнопка перехода на экран сравнения
                    IconButton(onClick = { navController.navigate("comparison") }) {
                        Icon(Icons.Default.BarChart, contentDescription = "Сравнить")
                    }
                    // Кнопка обновления (без анимации вращения, чтобы избежать ошибок)
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Поле поиска
            OutlinedTextField(
                value = filterText,
                onValueChange = { viewModel.updateFilter(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Поиск по коду валюты") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            // Контент
            if (currencies.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет валют, нажмите +")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(currencies, key = { it.id }) { currency ->
                        CurrencyCard(
                            currency = currency,
                            onFavorite = { viewModel.toggleFavorite(currency) },
                            onEdit = { editingCurrency = currency },
                            onDelete = { currencyToDelete = currency },
                            onHistory = { navController.navigate("history/${currency.id}") }
                        )
                    }
                }
            }
        }
    }

    // Диалоги (без изменений)
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
fun CurrencyCard(
    currency: Currency,
    onFavorite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onHistory: () -> Unit
) {
    // Анимация цвета звезды
    val animatedTint by animateColorAsState(
        targetValue = if (currency.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        animationSpec = tween(300),
        label = "star_tint"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = getFlagEmoji(currency.code), style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = currency.code,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onFavorite, modifier = Modifier.size(32.dp)) {
                    Icon(
                        if (currency.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Избранное",
                        tint = animatedTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Редактировать",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = "Курс: ${String.format(Locale.getDefault(), "%.2f", currency.rate)}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onHistory,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("История", maxLines = 1)
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CurrencyDialog(
    title: String,
    initialCode: String = "",
    onConfirm: (code: String, iconRes: Int?) -> Unit,
    onDismiss: () -> Unit
) {
    var code by remember { mutableStateOf(initialCode) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = code,
                onValueChange = { if (it.length <= 3) code = it },
                label = { Text("Код валюты (3 буквы)") },
                singleLine = true
            )
        },
        confirmButton = {
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

fun getFlagEmoji(currencyCode: String): String {
    if (currencyCode.length < 2) return "💰"
    val countryCode = when (currencyCode.uppercase()) {
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
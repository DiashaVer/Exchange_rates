package com.example.exchangerates.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchangerates.data.repository.CurrencyRepository
import com.example.exchangerates.domain.model.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // ID валюты, переданный через навигацию (может быть пустым)
    private val initialCurrencyId: String = savedStateHandle["currencyId"] ?: ""

    // Список всех валют для выпадающего списка
    private val _allCurrencies = MutableStateFlow<List<Currency>>(emptyList())
    val allCurrencies: StateFlow<List<Currency>> = _allCurrencies

    // Текущая выбранная валюта
    private val _selectedCurrency = MutableStateFlow<Currency?>(null)
    val selectedCurrency: StateFlow<Currency?> = _selectedCurrency

    // История для выбранной валюты (в виде Ui-модели)
    private val _history = MutableStateFlow<List<HistoryEntryUi>>(emptyList())
    val history: StateFlow<List<HistoryEntryUi>> = _history

    init {
        loadAllCurrencies()
    }

    private fun loadAllCurrencies() {
        viewModelScope.launch {
            repository.getAllCurrencies()
                .collect { currencies ->
                    _allCurrencies.value = currencies
                    // Выбор валюты: сначала по переданному id, иначе первая в списке
                    val target = if (initialCurrencyId.isNotBlank()) {
                        currencies.find { it.id == initialCurrencyId }
                    } else null
                    val selected = target ?: currencies.firstOrNull()
                    if (selected != null && selected.id != _selectedCurrency.value?.id) {
                        _selectedCurrency.value = selected
                        loadHistoryForCurrency(selected.id)
                    }
                }
        }
    }

    fun selectCurrency(currency: Currency) {
        if (_selectedCurrency.value?.id == currency.id) return
        _selectedCurrency.value = currency
        loadHistoryForCurrency(currency.id)
    }

    private fun loadHistoryForCurrency(currencyId: String) {
        viewModelScope.launch {
            repository.getHistoryForCurrency(currencyId)
                .map { entities ->
                    entities.map { entity ->
                        HistoryEntryUi(
                            date = LocalDate.parse(entity.date, DateTimeFormatter.ISO_LOCAL_DATE),
                            rate = entity.rate
                        )
                    }.sortedByDescending { it.date } // сначала новые
                }
                .collect { historyEntries ->
                    _history.value = historyEntries
                }
        }
    }
}

// Модель для отображения в UI
data class HistoryEntryUi(
    val date: LocalDate,
    val rate: Double
)
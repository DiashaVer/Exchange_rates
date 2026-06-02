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

/*
    Часть 2.1 – Фрагмент/экран исторических курсов выбранной валюты.
    Часть 2.3 – Передача данных между фрагментами через Bundle/SavedStateHandle
    Часть 4.2 – MVVM
    Часть 4.3 – DI через Hilt (@HiltViewModel, @Inject).
    Часть 4.4 – Возможность быстро переключаться между валютами (метод selectCurrency, выпадающий список в UI).
    Часть 5.1 – Получение исторических данных для построения графиков (через repository.getHistoryForCurrency).
*/

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    private val savedStateHandle: SavedStateHandle   // передача аргументов (часть 2.3)
) : ViewModel() {

    // ID валюты, переданный через навигацию
    private val initialCurrencyId: String = savedStateHandle["currencyId"] ?: ""

    // Список всех валют для выпадающего списка (часть 4.4 – быстрое переключение)
    private val _allCurrencies = MutableStateFlow<List<Currency>>(emptyList())
    val allCurrencies: StateFlow<List<Currency>> = _allCurrencies

    // Текущая выбранная валюта (для отображения истории)
    private val _selectedCurrency = MutableStateFlow<Currency?>(null)
    val selectedCurrency: StateFlow<Currency?> = _selectedCurrency

    // История для выбранной валюты (преобразована в Ui-модель для графика и списка)
    private val _history = MutableStateFlow<List<HistoryEntryUi>>(emptyList())
    val history: StateFlow<List<HistoryEntryUi>> = _history //5.1 подставляет данные для графика

    init {
        loadAllCurrencies()
    }

    // Загрузка всех доступных валют из репозитория (часть 2.1 – для выбора)
    private fun loadAllCurrencies() {
        viewModelScope.launch {
            repository.getAllCurrencies()
                .collect { currencies ->
                    _allCurrencies.value = currencies
                    // Выбор валюты: сначала по переданному id (из навигации), иначе первая в списке
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

    // Быстрое переключение между валютами (часть 4.4)
    fun selectCurrency(currency: Currency) {
        if (_selectedCurrency.value?.id == currency.id) return
        _selectedCurrency.value = currency
        loadHistoryForCurrency(currency.id)
    }

    // Загрузка исторических курсов для выбранной валюты (часть 2.1)
    // и преобразование в Ui-модель для графика (часть 5.1)
    private fun loadHistoryForCurrency(currencyId: String) {
        viewModelScope.launch {
            repository.getHistoryForCurrency(currencyId)
                .map { entities ->
                    entities.map { entity ->
                        HistoryEntryUi(
                            date = LocalDate.parse(entity.date, DateTimeFormatter.ISO_LOCAL_DATE),
                            rate = entity.rate
                        )
                    }.sortedByDescending { it.date } // сортировка: сначала новые (часть 4.4 – сортировка по дате)
                }
                .collect { historyEntries ->
                    _history.value = historyEntries
                }
        }
    }
}

// Модель для отображения в UI (график + карточки истории)
data class HistoryEntryUi(
    val date: LocalDate,
    val rate: Double
)
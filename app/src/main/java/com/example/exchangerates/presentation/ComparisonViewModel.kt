package com.example.exchangerates.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchangerates.data.repository.CurrencyRepository
import com.example.exchangerates.domain.model.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
    Часть 5.2 – Поддержка отображения нескольких валют одновременно для сравнения.
    ViewModel управляет выбранными валютами и загружает для них исторические данные (графики).

    Часть 4.2 – MVVM: ViewModel управляет состоянием данных,
    UI подписывается на StateFlow (аналог LiveData/Flow).

    Часть 4.3 – Dependency Injection через Hilt (@HiltViewModel, @Inject constructor).

    Часть 2.1 – Использует репозиторий для получения исторических курсов выбранной валюты.
    Часть 5.1 – Данные истории используются для построения графиков (LineChart).
*/

@HiltViewModel
class ComparisonViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {

    // Список всех доступных валют (для отображения чекбоксов)
    private val _allCurrencies = MutableStateFlow<List<Currency>>(emptyList())
    val allCurrencies: StateFlow<List<Currency>> = _allCurrencies

    // Множество ID выбранных валют (часть 5.2 – выбор нескольких)
    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds: StateFlow<Set<String>> = _selectedIds

    // История для каждой выбранной валюты (ключ – currencyId, значение – список записей для графика)
    private val _historyData = MutableStateFlow<Map<String, List<ComparisonHistoryEntry>>>(emptyMap())
    val historyData: StateFlow<Map<String, List<ComparisonHistoryEntry>>> = _historyData

    init {
        loadAllCurrencies()
    }

    // Загрузка всех валют из репозитория (через Flow, подписка на изменения)
    private fun loadAllCurrencies() {
        viewModelScope.launch {
            repository.getAllCurrencies()
                .collect { currencies ->
                    _allCurrencies.value = currencies
                }
        }
    }

    // Переключение выбора валюты (добавить/удалить из сравнения)
    fun toggleCurrency(currencyId: String) {
        val newSet = _selectedIds.value.toMutableSet()
        if (newSet.contains(currencyId)) newSet.remove(currencyId)
        else newSet.add(currencyId)
        _selectedIds.value = newSet
        // Для каждой выбранной валюты загружаем историю (если ещё не загружена)
        newSet.forEach { id ->
            if (!_historyData.value.containsKey(id)) {
                loadHistoryForCurrency(id)
            }
        }
        // Удаляем историю для валют, которые больше не выбраны
        _historyData.value = _historyData.value.filterKeys { newSet.contains(it) }
    }

    // Загрузка исторических курсов для конкретной валюты (часть 2.1)
    // и преобразование в ComparisonHistoryEntry для графика (часть 5.1)
    private fun loadHistoryForCurrency(currencyId: String) {
        viewModelScope.launch {
            repository.getHistoryForCurrency(currencyId)
                .map { entities ->
                    entities.map { entity ->
                        ComparisonHistoryEntry(
                            date = entity.date,
                            rate = entity.rate
                        )
                    }.sortedByDescending { it.date }
                }
                .collect { entries ->
                    _historyData.value = _historyData.value.toMutableMap().apply {
                        put(currencyId, entries)
                    }
                }
        }
    }
}

// DTO для истории, используемый в графиках (часть 5.1)
data class ComparisonHistoryEntry(
    val date: String,
    val rate: Double
)
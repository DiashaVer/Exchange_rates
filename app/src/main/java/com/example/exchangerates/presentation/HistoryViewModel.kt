package com.example.exchangerates.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random

class HistoryViewModel(private val currencyCode: String) : ViewModel() { //реализация часть 2 история курса валюты
    private val _historyRates = MutableStateFlow<List<Pair<LocalDate, Double>>>(emptyList()) // дата+число
    val historyRates = _historyRates.asStateFlow()

    init { //генерация истории курса
        loadHistory()
    }

    private fun loadHistory() { //логика случайного курса часть 1.2
        viewModelScope.launch {
            val today = LocalDate.now()
            val baseRate = 100.0
            val history = (1..7).map { daysAgo ->
                val date = today.minusDays(daysAgo.toLong())
                val change = Random.nextDouble(-0.05, 0.05)
                date to (baseRate * (1 + change)) //дата+цена
            }.reversed()
            _historyRates.value = history
        }
    }
}

class HistoryViewModelFactory(private val currencyCode: String) : ViewModelProvider.Factory { //передача данных курса валюты часть 2
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryViewModel(currencyCode) as T //передача кода валюты из навигации
    }
}
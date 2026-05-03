package com.example.exchangerates.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchangerates.data.repository.CurrencyRepository
import com.example.exchangerates.domain.model.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random
import javax.inject.Inject

enum class SortType { BY_CODE, BY_RATE, BY_FAVORITE }

@HiltViewModel
class CurrencyListViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {

    private val _filterText = MutableStateFlow("")
    val filterText: StateFlow<String> = _filterText.asStateFlow()

    private val _sortType = MutableStateFlow(SortType.BY_CODE)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()

    private val _rawCurrencies = MutableStateFlow<List<Currency>>(emptyList())
    private val _currencies = MutableStateFlow<List<Currency>>(emptyList())
    val currencies: StateFlow<List<Currency>> = _currencies

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    init {
        loadCurrencies()
        combineFilterAndSort()
        startPeriodicRefresh()
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            repository.getSortedCurrencies()
                .catch { e -> _snackbarMessage.emit("Ошибка загрузки: ${e.message}") }
                .collect { list -> _rawCurrencies.value = list }
        }
    }

    private fun combineFilterAndSort() {
        viewModelScope.launch {
            combine(_rawCurrencies, _filterText, _sortType) { raw, filter, sort ->
                var filtered = raw
                if (filter.isNotBlank()) {
                    filtered = filtered.filter { it.code.contains(filter, ignoreCase = true) }
                }
                when (sort) {
                    SortType.BY_CODE -> filtered.sortedBy { it.code }
                    SortType.BY_RATE -> filtered.sortedBy { it.rate }
                    SortType.BY_FAVORITE -> filtered.sortedWith(compareByDescending<Currency> { it.isFavorite }.thenBy { it.code })
                }
            }.collect { sorted -> _currencies.value = sorted }
        }
    }

    fun updateFilter(text: String) { _filterText.value = text }
    fun updateSortType(type: SortType) { _sortType.value = type }

    private fun startPeriodicRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(60_000)
                refresh()
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                repository.refreshFromNetwork()
                _snackbarMessage.emit("Курсы обновлены")
            } catch (e: Exception) {
                _snackbarMessage.emit(e.message ?: "Ошибка обновления")
            }
        }
    }

    fun addCurrency(code: String, iconRes: Int?) {
        viewModelScope.launch {
            try {
                val randomRate = Random.nextDouble(1.0, 200.0)
                repository.addCurrency(code, randomRate, iconRes)
                _snackbarMessage.emit("Добавлена $code, курс ${String.format(Locale.getDefault(), "%.2f", randomRate)}")
            } catch (e: Exception) {
                _snackbarMessage.emit("Ошибка добавления: ${e.message}")
            }
        }
    }

    fun updateCurrency(currency: Currency, newCode: String, newIconRes: Int?) {
        viewModelScope.launch {
            try {
                val newRate = Random.nextDouble(1.0, 200.0)
                repository.updateCurrency(currency, newCode, newRate, newIconRes)
                _snackbarMessage.emit("Изменено на $newCode, курс ${String.format(Locale.getDefault(), "%.2f", newRate)}")
            } catch (e: Exception) {
                _snackbarMessage.emit("Ошибка редактирования: ${e.message}")
            }
        }
    }

    fun deleteCurrency(currency: Currency) {
        viewModelScope.launch {
            try {
                repository.deleteCurrency(currency)
                _snackbarMessage.emit("${currency.code} удалена")
            } catch (e: Exception) {
                _snackbarMessage.emit("Ошибка удаления: ${e.message}")
            }
        }
    }

    fun toggleFavorite(currency: Currency) {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(currency)
                val msg = if (currency.isFavorite) "удалена из избранного" else "добавлена в избранное"
                _snackbarMessage.emit("${currency.code} $msg")
            } catch (e: Exception) {
                _snackbarMessage.emit("Ошибка: ${e.message}")
            }
        }
    }
}
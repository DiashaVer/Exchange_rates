package com.example.exchangerates.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchangerates.data.repository.CurrencyRepository
import com.example.exchangerates.domain.model.Currency
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import java.util.Locale

class CurrencyListViewModel(private val repository: CurrencyRepository) : ViewModel() {

    private val _snackbarMessage = MutableSharedFlow<String>() //текст сообщений помещаем
    val snackbarMessage = _snackbarMessage.asSharedFlow() //логика уведомлений часть 2.6

    fun addCurrency(code: String, iconRes: Int?) { //логика добавления валюты
        val randomRate = Random.nextDouble(1.0, 200.0)
        repository.addCurrency(code, randomRate, iconRes)
        viewModelScope.launch {
            _snackbarMessage.emit("Добавлена $code, курс ${String.format(Locale.getDefault(), "%.2f", randomRate)}")
        }
    }

    fun updateCurrency(currency: Currency, newCode: String, newIconRes: Int?) {
        val newRate = Random.nextDouble(1.0, 200.0)
        repository.updateCurrency(currency, newCode, newRate, newIconRes)
        viewModelScope.launch {
            _snackbarMessage.emit("Изменено на $newCode, курс ${String.format(Locale.getDefault(), "%.2f", newRate)}")
        }
    }

    fun deleteCurrency(currency: Currency) {
        repository.deleteCurrency(currency)
        viewModelScope.launch {
            _snackbarMessage.emit("${currency.code} удалена")
        }
    }

    fun toggleFavorite(currency: Currency) {
        repository.toggleFavorite(currency)
        val msg = if (currency.isFavorite) "удалена из избранного" else "добавлена в избранное"
        viewModelScope.launch {
            _snackbarMessage.emit("${currency.code} $msg")
        }
    }

    fun getSortedCurrencies() = repository.getSortedCurrencies()
}
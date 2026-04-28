package com.example.exchangerates.data.repository

import android.content.SharedPreferences // для хранения в  памяти
import androidx.compose.runtime.mutableStateListOf //для обновления списка на экране
import com.example.exchangerates.domain.model.Currency
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CurrencyRepository(private val prefs: SharedPreferences) {

    private val gson = Gson()
    private val currenciesKey = "currencies_list"

    val currencies = mutableStateListOf<Currency>()

    init {
        loadFromPrefs()
        if (currencies.isEmpty()) {
            addDefaultCurrencies()
        }
    }

    private fun addDefaultCurrencies() {
        val defaultList = listOf(
            Currency(code = "USD", rate = 92.50),
            Currency(code = "EUR", rate = 100.20),
            Currency(code = "GBP", rate = 118.30)
        )
        currencies.addAll(defaultList)
        saveToPrefs()
    }

    fun addCurrency(code: String, rate: Double, iconRes: Int?) {
        currencies.add(Currency(code = code.uppercase(), rate = rate, iconRes = iconRes))
        saveToPrefs()
    }

    fun updateCurrency(currency: Currency, newCode: String, newRate: Double, newIconRes: Int?) {
        val index = currencies.indexOfFirst { it.id == currency.id }
        if (index != -1) {
            currencies[index] = currency.copy(
                code = newCode.uppercase(),
                rate = newRate,
                iconRes = newIconRes,
                isFavorite = currency.isFavorite
            )
            saveToPrefs()
        }
    }

    fun deleteCurrency(currency: Currency) {
        currencies.remove(currency)
        saveToPrefs()
    }

    fun toggleFavorite(currency: Currency) { //часть 2.5 избранное
        val index = currencies.indexOfFirst { it.id == currency.id }
        if (index != -1) {
            currencies[index] = currency.copy(isFavorite = !currency.isFavorite)
            saveToPrefs()
        }
    }

    fun getSortedCurrencies(): List<Currency> { //часть 2.5 избранное отмечать ДО обычных в сетке
        return currencies.sortedWith(compareByDescending<Currency> { it.isFavorite }.thenBy { it.code })
    }

    private fun saveToPrefs() {
        val json = gson.toJson(currencies.toList())
        prefs.edit().putString(currenciesKey, json).apply()
    }

    private fun loadFromPrefs() { //часть 2.4 локальное сохранение
        val json = prefs.getString(currenciesKey, null)
        if (json != null) {
            val type = object : TypeToken<List<Currency>>() {}.type
            val list: List<Currency> = gson.fromJson(json, type)
            currencies.clear()
            currencies.addAll(list)
        }
    }
}
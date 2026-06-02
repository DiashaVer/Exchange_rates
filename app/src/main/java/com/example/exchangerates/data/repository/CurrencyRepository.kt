package com.example.exchangerates.data.repository

import android.content.Context
import android.util.Log
import com.example.exchangerates.data.api.ExchangeRateApi
import com.example.exchangerates.data.api.model.ExchangeRateDto
import com.example.exchangerates.data.local.dao.HistoryDao
import com.example.exchangerates.data.local.dao.RateDao
import com.example.exchangerates.data.local.entity.HistoryEntity
import com.example.exchangerates.data.local.entity.RateEntity
import com.example.exchangerates.domain.model.Currency
import com.example.exchangerates.utils.NotificationHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class CurrencyRepository @Inject constructor(
    private val api: ExchangeRateApi,
    private val rateDao: RateDao,
    private val historyDao: HistoryDao,
    @ApplicationContext private val context: Context
) {

    fun getSortedCurrencies(): Flow<List<Currency>> {
        return rateDao.getAll().map { entities ->
            entities.map { it.toCurrency() }
                .sortedWith(compareByDescending<Currency> { it.isFavorite }.thenBy { it.code })
        }
    }

    fun getAllCurrencies(): Flow<List<Currency>> {
        return rateDao.getAll().map { entities -> entities.map { it.toCurrency() } }
    }

    fun getHistoryForCurrency(currencyId: String): Flow<List<HistoryEntity>> {
        return historyDao.getHistoryForCurrency(currencyId)
    }

    suspend fun refreshFromNetwork() { //часть 5 для уведомлений. загружает курсы и сравнвиает старые с новыми
        Log.d("CurrencyRepository", "refreshFromNetwork вызван в ${System.currentTimeMillis()}")
        try {
            val oldRatesMap = rateDao.getAll().first().associate { it.id to it.rate }
            Log.d("CurrencyRepository", "Старые курсы: $oldRatesMap")
            val response = api.getRates()
            val serverRates = response.currencies
            mergeAndSave(serverRates)
            val newRatesMap = rateDao.getAll().first().associate { it.id to it.rate }
            Log.d("CurrencyRepository", "Новые курсы: $newRatesMap")
            checkAndNotify(oldRatesMap, newRatesMap, serverRates)
        } catch (e: Exception) {
            Log.e("CurrencyRepository", "Ошибка обновления", e)
            throw Exception("Не удалось обновить: ${e.message}")
        }
    }

    private suspend fun checkAndNotify( //вычисление процента измененяи для каждой валюты
        oldRatesMap: Map<String, Double>,
        newRatesMap: Map<String, Double>,
        serverRates: List<ExchangeRateDto>
    ) {
        Log.d("CurrencyRepository", "checkAndNotify: old=$oldRatesMap, new=$newRatesMap")
        val codeMap = serverRates.associate { it.id to it.code }
        newRatesMap.forEach { (id, newRate) ->
            val oldRate = oldRatesMap[id]
            if (oldRate != null) {
                val changePercent = (newRate - oldRate) / oldRate * 100 //формула вычисления изменения
                val currencyCode = codeMap[id] ?: id
                Log.d("CurrencyRepository", "Валюта $currencyCode: изменение = ${String.format("%.2f", changePercent)}%")
                if (abs(changePercent) >= 5.0) { //5% , временно было 0.01 для проверки уведомлений
                    val oldRateStr = String.format("%.2f", oldRate)
                    val newRateStr = String.format("%.2f", newRate)
                    NotificationHelper.sendNotification(
                        context,
                        "Курс $currencyCode изменился",
                        "Было: $oldRateStr → Стало: $newRateStr (${String.format("%.2f", changePercent)}%)"
                    )
                        // NotificationHelper.sendNotification(
                        //context,
                      //  "Курс $currencyCode изменился",
                        //String.format(Locale.getDefault(), "Изменение: %.2f%%", changePercent)
                   // )
                } else {
                    Log.d("CurrencyRepository", "Изменение менее 5% – уведомление не требуется")
                }
            }
        }
    }

    private suspend fun mergeAndSave(serverRates: List<ExchangeRateDto>) {
        val localRates = rateDao.getAll().first().map { it.toCurrency() }
        val merged = serverRates.map { serverRate ->
            val local = localRates.find { it.id == serverRate.id }
            Currency(
                id = serverRate.id,
                code = serverRate.code,
                rate = serverRate.currentRate,
                iconRes = local?.iconRes,
                isFavorite = local?.isFavorite ?: false
            )
        }
        rateDao.clearAll()
        merged.forEach { currency ->
            rateDao.insert(RateEntity.fromCurrency(currency))
        }

        serverRates.forEach { serverRate ->
            historyDao.deleteHistoryForCurrency(serverRate.id)
            val historyEntities = serverRate.history.map { entry ->
                HistoryEntity.fromHistoryEntry(serverRate.id, entry)
            }
            if (historyEntities.isNotEmpty()) {
                historyDao.insertAll(historyEntities)
            }
        }
    }

    suspend fun addCurrency(code: String, rate: Double, iconRes: Int?) {
        val newId = UUID.randomUUID().toString()
        val currency = Currency(newId, code.uppercase(), rate, iconRes, false)
        rateDao.insert(RateEntity.fromCurrency(currency))
    }

    suspend fun updateCurrency(currency: Currency, newCode: String, newRate: Double, newIconRes: Int?) {
        val updated = currency.copy(code = newCode.uppercase(), rate = newRate, iconRes = newIconRes)
        rateDao.update(RateEntity.fromCurrency(updated))
    }

    suspend fun deleteCurrency(currency: Currency) {
        rateDao.delete(RateEntity.fromCurrency(currency))
    }

    suspend fun toggleFavorite(currency: Currency) {
        val updated = currency.copy(isFavorite = !currency.isFavorite)
        rateDao.update(RateEntity.fromCurrency(updated))
    }
}
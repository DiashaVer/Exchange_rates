package com.example.exchangerates.data.local.dao

import androidx.room.*
import com.example.exchangerates.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow


@Dao //4.1 интерфейсы для доступа к данным ЛБД рум
interface HistoryDao {

    /*
    Часть 2.5 – Выбор валюты и отображение её истории.
    Часть 4.4 – Сортировка по дате (ORDER BY date DESC).
    Возвращает Flow – при изменении данных в БД обновления автоматически приходят в UI (MVVM).
    */
    @Query("SELECT * FROM history WHERE currencyId = :currencyId ORDER BY date DESC")
    fun getHistoryForCurrency(currencyId: String): Flow<List<HistoryEntity>>


    /*
      Часть 3.8 – Объединение загруженных данных с локальными.
      OnConflictStrategy.REPLACE обновляет запись по id.
      Часть 4.1 – Кэширование исторических значений.
    */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: HistoryEntity)

    /*
     Часть 3.8 – Пакетное обновление/сохранение истории для нескольких валют.
     Часть 4.1 – Кэширование.
    */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(historyList: List<HistoryEntity>)

    /*
        Часть 2.1 – При удалении валюты должна удаляться и её история (опционально).
        Часть 4.1 – Управление кэшем.
    */
    @Query("DELETE FROM history WHERE currencyId = :currencyId")
    suspend fun deleteHistoryForCurrency(currencyId: String)
}
package com.example.exchangerates.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.exchangerates.data.api.model.HistoryEntryDto

/*
    Часть 4.1 – Использование Room для кэширования исторических курсов валют.
    Таблица "history" хранит исторические значения для каждой валюты.

    Часть 3. 8 – Объединение загруженных с сервера данных с локальными.
    DTO (HistoryEntryDto) преобразуется в Entity для сохранения в БД.

    Часть 2.1 – Отображение исторических курсов выбранной валюты.
    Часть 5.1 – Графики исторических курсов (необходимы данные дата+курс).
*/

@Entity(tableName = "history") //4.1
data class HistoryEntity(
    @PrimaryKey val id: String, // уникальный ID записи (часть 3.6)
    val currencyId: String,     // связь с таблицей валют (внешний ключ)
    val date: String,           // дата для сортировки/фильтрации (часть 4.4)
    val rate: Double            // значение курса на указанную дату
) {

    /*
    Часть 3.3 – Преобразование Entity в DTO для отображения в UI.
    Используется при извлечении данных из БД.
*/
    fun toHistoryEntry() = HistoryEntryDto(id, date, rate)

    companion object {
        /*
    Часть 3.8 – Преобразование DTO из API в Entity для сохранения в Room.
    Также используется при кэшировании (часть 4.1).
*/
        fun fromHistoryEntry(currencyId: String, entry: HistoryEntryDto) = HistoryEntity(
            id = entry.id,
            currencyId = currencyId,
            date = entry.date,
            rate = entry.rate
        )
    }
}
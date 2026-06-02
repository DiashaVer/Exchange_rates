package com.example.exchangerates.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.exchangerates.domain.model.Currency

/*
    Часть 4.1 – Использование Room для кэширования текущих курсов валют.
    Таблица "rates" хранит список валют с их текущими курсами.

    Часть 2.4 – Локальное сохранение списка валют между перезапусками.
    Часть 2.5 – Добавление отметки "избранное" (поле isFavorite) и его сохранение.
    Часть 1.5 – Хранение иконки валюты (поле iconRes).
    Часть 3.6 – Уникальный id (UUID или строка).
*/
@Entity(tableName = "rates") // 4.1
data class RateEntity(
    @PrimaryKey val id: String,     // уникальный идентификатор валюты (часть 3.6)
    val code: String,   // трёхбуквенный код (часть 1.5)
    val rate: Double,   // текущий курс (часть 1.5)
    val isFavorite: Boolean = false,    // избранное – часть 2.5
    val iconRes: Int? = null    // иконка (ресурс) – часть 1.5
) {

    /*
    Часть 4.2 – Преобразование Entity в модель домена (для ViewModel/UI).
*/
    fun toCurrency() = Currency(id, code, rate, iconRes, isFavorite)

    companion object {
        /*
    Часть 4.2 – Преобразование доменной модели в Entity для сохранения в БД.
    Используется при вставке/обновлении данных.
*/
        fun fromCurrency(currency: Currency) = RateEntity(
            id = currency.id,
            code = currency.code,
            rate = currency.rate,
            isFavorite = currency.isFavorite,
            iconRes = currency.iconRes
        )
    }
}
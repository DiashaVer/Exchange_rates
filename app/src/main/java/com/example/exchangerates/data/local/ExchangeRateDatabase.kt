package com.example.exchangerates.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.exchangerates.data.local.dao.HistoryDao
import com.example.exchangerates.data.local.dao.RateDao
import com.example.exchangerates.data.local.entity.HistoryEntity
import com.example.exchangerates.data.local.entity.RateEntity

/*
    Часть 4.1 – Использование Room для кэширования текущих и исторических курсов валют.
    Также это основа для локального сохранения (часть 2.4) между перезапусками.
    Часть 3.8 – Загруженные данные объединяются с локальными через DAO.
    Часть 2.5 – Состояние "избранное" сохраняется в этой БД (RateEntity.isFavorite).
*/
@Database( //4.1
    entities = [RateEntity::class, HistoryEntity::class],   // две таблицы: валюты и история
    version = 2,            // версия 2 – возможность миграции (добавлены isFavorite или связь)
    exportSchema = false        // для упрощения (не экспортируем схему в JSON)
)
abstract class ExchangeRateDatabase : RoomDatabase() {
    /*
    Часть 4.2 – DAO предоставляют Flow / suspend функции для использования в ViewModel.
    RateDao – CRUD операции и наблюдение за списком валют (часть 1.2).
*/
    abstract fun rateDao(): RateDao
    /*
    Часть 2.1 – DAO для истории выбранной валюты.
    Часть 5.1 – данные для графиков.
*/
    abstract fun historyDao(): HistoryDao
}
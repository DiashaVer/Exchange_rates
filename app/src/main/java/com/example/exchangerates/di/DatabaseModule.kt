package com.example.exchangerates.di

import android.content.Context
import androidx.room.Room
import com.example.exchangerates.data.local.ExchangeRateDatabase
import com.example.exchangerates.data.local.dao.HistoryDao
import com.example.exchangerates.data.local.dao.RateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /*
        Часть 4.1 – Использование Room для кэширования текущих и исторических курсов.
        Предоставляем синглтон базы данных. БД сохраняется между перезапусками (часть 2.4).

    */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ExchangeRateDatabase {
        return Room.databaseBuilder(
            context,
            ExchangeRateDatabase::class.java,
            "exchange_rate_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    /*
        Часть 4.2 – Предоставление DAO для валют (RateDao).
        Используется в репозитории для CRUD (часть 1.2) и наблюдения за данными (Flow/LiveData).
        Также обеспечивает сохранение избранного (часть 2.5) – поле isFavorite хранится в Room.
    */
    @Provides
    @Singleton
    fun provideRateDao(database: ExchangeRateDatabase): RateDao {
        return database.rateDao()
    }

    /*
        Часть 4.2 – Предоставление DAO для истории (HistoryDao).
        Используется для получения исторических курсов выбранной валюты (часть 2.1)
        и для кэширования данных для графиков (часть 5.1).
    */
    @Provides
    @Singleton
    fun provideHistoryDao(database: ExchangeRateDatabase): HistoryDao {
        return database.historyDao()
    }
}
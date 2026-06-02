package com.example.exchangerates.di

import com.example.exchangerates.data.api.ExchangeRateApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory //часть 3.7
import javax.inject.Singleton

/*
    Часть 4.3 – Настройка Dependency Injection (Hilt).
    Данный модуль предоставляет сетевые зависимости для работы с сервером (часть 3).
*/

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /*
        Часть 3.1 – Получение данных в формате JSON.
        Moshi преобразует JSON в Kotlin data class (ExchangeRateResponse, ExchangeRateDto и т.д.).
        KotlinJsonAdapterFactory нужен для работы с data классами и nullable полями.
    */
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    /*
        Часть 3.1 – Базовый URL API (задан в задании).
        Часть 3.2 – Retrofit + Coroutines (через suspend-функции в Api).
        MoshiConverterFactory десериализует JSON в DTO.
    */
    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://gateway-staging.superglue.games/android/api/v1/public/mocks/") // часть 3.1 базовый URL
            .addConverterFactory(MoshiConverterFactory.create(moshi)) // для работы с JSON и часть 3.2
            .build()
    }

    /*
        Часть 3.2 – Создание экземпляра API для выполнения запросов.
        Внедряется в репозиторий (CurrencyRepository).
    */
    @Provides
    @Singleton
    fun provideExchangeRateApi(retrofit: Retrofit): ExchangeRateApi {
        return retrofit.create(ExchangeRateApi::class.java)
    }
}
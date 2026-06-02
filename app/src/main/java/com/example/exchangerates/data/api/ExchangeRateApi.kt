package com.example.exchangerates.data.api

import com.example.exchangerates.data.api.model.ExchangeRateResponse
import retrofit2.http.GET


/*
 3.1-3.2
*/
interface ExchangeRateApi {
    @GET("exchange-rates")  // 3.1 объясвление HTTP GET запроса
    suspend fun getRates(): ExchangeRateResponse // возвращает DTO с курсами и историей 3.2 и 3.7
    //корутина, грузит данные, умное переключение
}
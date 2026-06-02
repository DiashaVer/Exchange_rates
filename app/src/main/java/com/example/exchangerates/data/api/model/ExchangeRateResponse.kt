package com.example.exchangerates.data.api.model

//обвертка для списка валют из API (хранение списка)
data class ExchangeRateResponse(
    val currencies: List<ExchangeRateDto> //список валют, который вернет сервер часть 3.1
)
package com.example.exchangerates.data.api.model

import com.squareup.moshi.Json

data class ExchangeRateDto(
    val id: String,         // часть 3.6 уникальный идентификатор валюты (UUID)
    val code: String,       // трёхбуквенный идентификатор валюты
    @Json(name = "currentRate") val currentRate: Double, // текущий курс
    val history: List<HistoryEntryDto>   // часть 3.1 исторические курсы для выбранной валюты и часть 5.1 (графики)
)


    // часть 3.1 и 5.1,
    // часть 2 - история курсов для построения графиков и отображения в отдельном фрагменте

data class HistoryEntryDto(
    val id: String,         // уникальный идентификатор записи истории
    val date: String,       // дата в формате "2026-04-05"
    val rate: Double        // занчение курса
)
package com.example.exchangerates.domain.model

import java.util.UUID

data class Currency( //часть 1.5 отображение информации
    val id: String = UUID.randomUUID().toString(),
    val code: String,
    val rate: Double,
    val iconRes: Int? = null,
    val isFavorite: Boolean = false
)
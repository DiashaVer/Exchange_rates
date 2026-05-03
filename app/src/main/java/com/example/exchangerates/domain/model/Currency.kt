package com.example.exchangerates.domain.model   // ← обязательно такой путь

import java.util.UUID

data class Currency(
    val id: String = UUID.randomUUID().toString(),
    val code: String,
    val rate: Double,
    val iconRes: Int? = null,
    val isFavorite: Boolean = false
)
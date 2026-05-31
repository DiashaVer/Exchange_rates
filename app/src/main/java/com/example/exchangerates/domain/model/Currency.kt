package com.example.exchangerates.domain.model

import java.util.UUID

/*
    Часть 1 – Базовая модель валюты для использования в UI и бизнес-логике.
    Часть 3.6 – Каждая валюта имеет уникальный id (UUID или строка).
    Часть 4.2 – Модель домена используется в ViewModel и передаётся во фрагменты.
    Часть 2.5 – Поле isFavorite для отметки "избранное".
    Часть 1.5 – Поле iconRes для иконки валюты.
*/

data class Currency(
    val id: String = UUID.randomUUID().toString(),  // уникальный ID (генерируется при создании)
    val code: String,          // трёхбуквенный код (USD, EUR и т.д.)
    val rate: Double,          // текущий курс
    val iconRes: Int? = null,  // ресурс иконки (целое число, ссылка на drawable)
    val isFavorite: Boolean = false  // статус избранного (отображать вверху списка)
)
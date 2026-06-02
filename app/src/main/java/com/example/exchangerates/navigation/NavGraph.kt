package com.example.exchangerates.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.exchangerates.presentation.CurrencyListScreen
import com.example.exchangerates.presentation.CurrencyListViewModel
import com.example.exchangerates.presentation.HistoryScreen
import com.example.exchangerates.presentation.ComparisonScreen

/*
    Часть 2.1 и 2.2 – Разделение приложения на экраны и навигация между ними.
    Вместо фрагментов (XML) используется Jetpack Compose с навигацией через NavHost.
    Эквивалентно фрагментам: каждый composable-экран выполняет роль отдельного "фрагмента".

    Часть 2.3 – Передача данных между экранами через аргументы маршрута (currencyId),
    что аналогично Bundle или SafeArgs.

    Часть 5.2 – Добавлен экран сравнения валют (ComparisonScreen) – отображение нескольких валют одновременно.

    Часть 4.2 – Используется hiltViewModel() для внедрения ViewModel (DI  Hilt).
*/

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val listViewModel: CurrencyListViewModel = hiltViewModel()  // DI, MVVM

    NavHost(
        navController = navController,
        startDestination = "currency_list",   // главный экран со списком валют
        modifier = modifier
    ) {
        // Часть 1 и 2 – главный экран (список валют с текущим курсом + сортировка)
        composable("currency_list") {
            CurrencyListScreen(
                navController = navController,
                viewModel = listViewModel
            )
        }

        // Часть 2.1 – экран исторических курсов выбранной валюты
        // Передача currencyId через аргумент (как Bundle/SafeArgs)
        composable(
            route = "history/{currencyId}",
            arguments = listOf(navArgument("currencyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val currencyId = backStackEntry.arguments?.getString("currencyId") ?: ""
            HistoryScreen(
                navController = navController,
                currencyId = currencyId
            )
        }

        // Часть 5.2 – экран сравнения нескольких валют одновременно
        composable("comparison") {
            ComparisonScreen(navController = navController)
        }
    }
}
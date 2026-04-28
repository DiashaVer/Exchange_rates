package com.example.exchangerates.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.exchangerates.data.repository.CurrencyRepository
import com.example.exchangerates.presentation.CurrencyListScreen
import com.example.exchangerates.presentation.CurrencyListViewModel
import com.example.exchangerates.presentation.HistoryScreen

@Composable //часть 2.1 на два фрагмента
fun AppNavGraph(modifier: Modifier = Modifier, repository: CurrencyRepository) {
    val navController = rememberNavController() //для переходов
    val listViewModel = CurrencyListViewModel(repository)

    NavHost( //часть 2.3 навигация между экранами
        navController = navController,
        startDestination = "currency_list",
        modifier = modifier
    ) {
        composable("currency_list") { //часть 2.1 список валют
            CurrencyListScreen(
                navController = navController,
                viewModel = listViewModel
            )
        }
        composable( //часть 2.1 история курсов
            route = "history/{currencyCode}",
            arguments = listOf(navArgument("currencyCode") { type = NavType.StringType })
        ) { backStackEntry -> //достаем данные
            val code = backStackEntry.arguments?.getString("currencyCode") ?: "USD"


            HistoryScreen(
                navController = navController, //для кнопки назад
                currencyCode = code
            )
        }
    }
}

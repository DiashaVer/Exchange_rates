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

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val listViewModel: CurrencyListViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "currency_list",
        modifier = modifier
    ) {
        composable("currency_list") {
            CurrencyListScreen(
                navController = navController,
                viewModel = listViewModel
            )
        }

        // Экран истории (передаём currencyId)
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

        // Экран сравнения валют
        composable("comparison") {
            ComparisonScreen(navController = navController)
        }
    }
}
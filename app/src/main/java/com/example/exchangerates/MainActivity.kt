package com.example.exchangerates

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.preference.PreferenceManager
import com.example.exchangerates.data.repository.CurrencyRepository
import com.example.exchangerates.navigation.AppNavGraph
import com.example.exchangerates.ui.theme.ExchangeRatesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExchangeRatesTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val prefs = PreferenceManager.getDefaultSharedPreferences(this) // инициализация SharedPreferences
                    val repository = CurrencyRepository(prefs) // для сохр и загруз
                    AppNavGraph(repository = repository) // для показа экрана
                }
            }
        }
    }
}
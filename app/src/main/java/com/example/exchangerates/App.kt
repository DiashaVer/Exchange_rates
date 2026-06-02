package com.example.exchangerates

import android.app.Application
import com.example.exchangerates.utils.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}
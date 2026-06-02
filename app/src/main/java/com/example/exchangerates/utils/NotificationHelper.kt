package com.example.exchangerates.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.exchangerates.MainActivity

object NotificationHelper {
    private const val CHANNEL_ID = "currency_channel"
    private const val CHANNEL_NAME = "Курсы валют"
    private var notificationId = 0

    fun createNotificationChannel(context: Context) { //создание канала для уведомлений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления об изменении курса валют"
                enableVibration(true)
                setShowBadge(true)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(context: Context, title: String, message: String) { //постройка уведомления с иконкой и прочим
        // Создаём Intent для открытия приложения при клике
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID) //отправка уведомлений
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)  // Без этого на Android 12+ уведомление может не показаться
            .setVibrate(longArrayOf(0, 500))
            .setSound(null)

        NotificationManagerCompat.from(context).notify(notificationId++, builder.build())
    }
}
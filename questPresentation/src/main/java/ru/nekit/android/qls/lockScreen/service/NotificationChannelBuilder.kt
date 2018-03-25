package ru.nekit.android.qls.lockScreen.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.support.annotation.RequiresApi

class NotificationChannelBuilder(private val notificationManager: NotificationManager) {

    companion object {
        private const val CHANNEL_ID = "ru.nekit.android.qls"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun create(name: String): String {
        val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
        return CHANNEL_ID
    }
}
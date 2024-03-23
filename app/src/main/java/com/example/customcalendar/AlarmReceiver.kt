package com.example.customcalendar

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val description = intent?.getStringExtra("EXTRA_DESCRIPTION")

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(context,"event_id")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Reminder \uD83D\uDE42")
            .setContentText(description)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAllowSystemGeneratedContextualActions(true)
            .build()


        notificationManager.notify(123,builder)
    }
}
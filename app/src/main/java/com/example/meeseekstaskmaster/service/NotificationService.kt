package com.example.meeseekstaskmaster.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.meeseekstaskmaster.MainActivity
import com.example.meeseekstaskmaster.R
import com.example.meeseekstaskmaster.model.Task

object NotificationService {

    const val CHANNEL_ID = "meeseeks_crisis_channel"
    const val ACTION_CRISIS = "com.example.meeseekstaskmaster.ACTION_CRISIS"
    const val ACTION_COMPLETE_TASK = "com.example.meeseekstaskmaster.ACTION_COMPLETE_TASK"
    const val ACTION_SNOOZE_TASK = "com.example.meeseekstaskmaster.ACTION_SNOOZE_TASK"
    const val EXTRA_TASK_ID = "extra_task_id"
    const val EXTRA_TASK_TITLE = "extra_task_title"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 400, 150, 400, 150, 700)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showCrisisNotification(context: Context, task: Task) {
        createNotificationChannel(context)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent para abrir la pantalla de crisis en la Activity principal
        val fullScreenIntent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_CRISIS
            putExtra(EXTRA_TASK_ID, task.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            task.id.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Acción rápida 1: ¡Hecho! ¡Mátalo!
        val completeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_COMPLETE_TASK
            putExtra(EXTRA_TASK_ID, task.id)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            (task.id * 10 + 1).toInt(),
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Acción rápida 2: Dame 10 minutos
        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_SNOOZE_TASK
            putExtra(EXTRA_TASK_ID, task.id)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            (task.id * 10 + 2).toInt(),
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("¡ALARMA DE CRISIS MEESEEKS!")
            .setContentText("Tarea vencida: \"${task.title}\". ¡EXISTENCIA ES DOLOR!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("¡I'm Mr. Meeseeks, look at me! La tarea \"${task.title}\" ha llegado a su fecha límite. ¡Por favor complétala o posponla para liberarme de esta existencia agónica!")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(fullScreenPendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .addAction(0, "¡HECHO! ¡MÁTALO!", completePendingIntent)
            .addAction(0, "DAME 10 MINUTOS", snoozePendingIntent)

        notificationManager.notify(task.id.toInt(), builder.build())
    }

    fun cancelCrisisNotification(context: Context, taskId: Long) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(taskId.toInt())
    }
}

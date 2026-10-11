package com.example.meeseekstaskmaster.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.meeseekstaskmaster.model.Task

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm(task: Task) {
        val now = System.currentTimeMillis()
        if (task.deadline <= now) {
            Log.w(TAG, "No se puede programar alarma para tarea en el pasado: ${task.title}")
            return
        }

        // Cancelamos cualquier alarma previa asociada a esta tarea
        cancelAlarm(task.id)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
            putExtra(NotificationService.EXTRA_TASK_ID, task.id)
            putExtra(NotificationService.EXTRA_TASK_TITLE, task.title)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        task.deadline,
                        pendingIntent
                    )
                } else {
                    alarmManager.setWindow(
                        AlarmManager.RTC_WAKEUP,
                        task.deadline,
                        60_000L,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    task.deadline,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    task.deadline,
                    pendingIntent
                )
            }
            Log.d(TAG, "Alarma programada exitosamente para tarea ${task.id} en ${task.deadline}")
        } catch (e: SecurityException) {
            Log.e(TAG, "Permiso exact alarm denegado, usando setWindow: ${e.message}")
            try {
                alarmManager.setWindow(
                    AlarmManager.RTC_WAKEUP,
                    task.deadline,
                    60_000L,
                    pendingIntent
                )
            } catch (fallbackException: Exception) {
                Log.e(TAG, "Error crítico al programar alarma: ${fallbackException.message}")
            }
        }
    }

    fun cancelAlarm(taskId: Long) {
        try {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = ACTION_TRIGGER_ALARM
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId.toInt(),
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
                Log.d(TAG, "Alarma cancelada para tarea $taskId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al cancelar alarma para tarea $taskId: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "AlarmScheduler"
        const val ACTION_TRIGGER_ALARM = "com.example.meeseekstaskmaster.ACTION_TRIGGER_ALARM"
    }
}

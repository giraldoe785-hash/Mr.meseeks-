package com.example.meeseekstaskmaster.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.meeseekstaskmaster.data.local.AppDatabase
import com.example.meeseekstaskmaster.data.repository.SettingsRepository
import com.example.meeseekstaskmaster.data.repository.TaskRepository
import com.example.meeseekstaskmaster.model.TaskStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val taskId = intent.getLongExtra(NotificationService.EXTRA_TASK_ID, -1L)

        Log.d(TAG, "onReceive action=$action taskId=$taskId")

        if (taskId == -1L) {
            return
        }

        val pendingResult = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getInstance(appContext)
                val alarmScheduler = AlarmScheduler(appContext)
                val repository = TaskRepository(database.taskDao(), alarmScheduler, appContext)
                val settings = SettingsRepository.getInstance(appContext)

                when (action) {
                    AlarmScheduler.ACTION_TRIGGER_ALARM -> {
                        val task = database.taskDao().getTaskById(taskId)
                        if (task != null && task.status == TaskStatus.PENDING) {
                            // Actualizamos el estado en base de datos
                            val overdueTask = task.copy(
                                status = TaskStatus.OVERDUE,
                                isAlarmTriggered = true
                            )
                            database.taskDao().updateTask(overdueTask)

                            // Reproducir efectos según preferencias del usuario
                            if (settings.isSoundEnabledSync()) {
                                AudioService.playAlarm(appContext)
                            }
                            if (settings.isVibrationEnabledSync()) {
                                VibrationService.startVibration(appContext)
                            }

                            // Mostrar notificación de alta prioridad / pantalla de crisis
                            NotificationService.showCrisisNotification(appContext, overdueTask)
                        }
                    }

                    NotificationService.ACTION_COMPLETE_TASK -> {
                        repository.completeTaskById(taskId)
                    }

                    NotificationService.ACTION_SNOOZE_TASK -> {
                        repository.postponeTask(taskId, 10)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al procesar acción en AlarmReceiver: ${e.message}", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val TAG = "AlarmReceiver"
    }
}

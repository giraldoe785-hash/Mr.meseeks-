package com.example.meeseekstaskmaster.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.meeseekstaskmaster.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON" ||
            intent.action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            val pendingResult = goAsync()
            val appContext = context.applicationContext

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = AppDatabase.getInstance(appContext)
                    val alarmScheduler = AlarmScheduler(appContext)
                    val pendingTasks = database.taskDao().getPendingTasks()
                    val now = System.currentTimeMillis()

                    var rescheduledCount = 0
                    for (task in pendingTasks) {
                        if (task.deadline > now) {
                            alarmScheduler.scheduleAlarm(task)
                            rescheduledCount++
                        }
                    }
                    Log.d(TAG, "Reprogramación completada tras reinicio: $rescheduledCount alarmas activas.")
                } catch (e: Exception) {
                    Log.e(TAG, "Error al restaurar alarmas tras el reinicio: ${e.message}", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}

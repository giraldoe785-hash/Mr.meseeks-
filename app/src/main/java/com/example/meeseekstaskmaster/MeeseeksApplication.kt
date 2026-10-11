package com.example.meeseekstaskmaster

import android.app.Application
import com.example.meeseekstaskmaster.data.local.AppDatabase
import com.example.meeseekstaskmaster.data.repository.SettingsRepository
import com.example.meeseekstaskmaster.data.repository.TaskRepository
import com.example.meeseekstaskmaster.service.AlarmScheduler
import com.example.meeseekstaskmaster.service.NotificationService

class MeeseeksApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var taskRepository: TaskRepository
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var alarmScheduler: AlarmScheduler
        private set

    override fun onCreate() {
        super.onCreate()
        // Crear canal de notificación de alta prioridad
        NotificationService.createNotificationChannel(this)

        // Inicializar persistencia y dependencias
        database = AppDatabase.getInstance(this)
        alarmScheduler = AlarmScheduler(this)
        settingsRepository = SettingsRepository.getInstance(this)
        taskRepository = TaskRepository(database.taskDao(), alarmScheduler, this)
    }
}

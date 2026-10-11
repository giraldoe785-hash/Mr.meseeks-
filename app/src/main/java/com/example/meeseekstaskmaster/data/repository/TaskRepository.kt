package com.example.meeseekstaskmaster.data.repository

import android.content.Context
import com.example.meeseekstaskmaster.data.local.TaskDao
import com.example.meeseekstaskmaster.model.Task
import com.example.meeseekstaskmaster.model.TaskStatus
import com.example.meeseekstaskmaster.service.AlarmScheduler
import com.example.meeseekstaskmaster.service.AudioService
import com.example.meeseekstaskmaster.service.NotificationService
import com.example.meeseekstaskmaster.service.VibrationService
import com.example.meeseekstaskmaster.util.TaskValidation
import com.example.meeseekstaskmaster.util.ValidationResult
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val alarmScheduler: AlarmScheduler? = null,
    private val context: Context? = null
) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasksFlow()
    val activeTasks: Flow<List<Task>> = taskDao.getActiveTasksFlow()
    val historyTasks: Flow<List<Task>> = taskDao.getHistoryTasksFlow()

    fun getTaskByIdFlow(id: Long): Flow<Task?> = taskDao.getTaskByIdFlow(id)

    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)

    suspend fun createTask(title: String, description: String, deadline: Long): Result<Task> {
        val validation = TaskValidation.validate(title, deadline)
        if (validation is ValidationResult.Error) {
            return Result.failure(IllegalArgumentException(validation.message))
        }

        val newTask = Task(
            title = title.trim(),
            description = description.trim(),
            createdAt = System.currentTimeMillis(),
            deadline = deadline,
            status = TaskStatus.PENDING
        )

        val insertedId = taskDao.insertTask(newTask)
        val createdTask = newTask.copy(id = insertedId)

        // Programar alarma nativa
        alarmScheduler?.scheduleAlarm(createdTask)

        return Result.success(createdTask)
    }

    suspend fun completeTask(task: Task) {
        val completedTask = task.copy(
            status = TaskStatus.COMPLETED,
            completedAt = System.currentTimeMillis()
        )
        taskDao.updateTask(completedTask)

        // Cancelar alarma asociada
        alarmScheduler?.cancelAlarm(task.id)

        // Detener notificación y efectos de crisis si estaban activos
        context?.let { ctx ->
            NotificationService.cancelCrisisNotification(ctx, task.id)
            AudioService.stopAudio()
            VibrationService.stopVibration(ctx)
        }
    }

    suspend fun completeTaskById(taskId: Long) {
        val task = taskDao.getTaskById(taskId) ?: return
        completeTask(task)
    }

    /**
     * Pospone la tarea exactamente [minutes] minutos después del momento actual,
     * persistiendo el cambio, cancelando la alarma previa y programando la nueva.
     */
    suspend fun postponeTask(taskId: Long, minutes: Int = 10): Result<Task> {
        val task = taskDao.getTaskById(taskId)
            ?: return Result.failure(NoSuchElementException("Tarea con ID $taskId no encontrada"))

        val newDeadline = System.currentTimeMillis() + (minutes * 60 * 1000L)
        val updatedTask = task.copy(
            deadline = newDeadline,
            status = TaskStatus.PENDING,
            isAlarmTriggered = false
        )

        taskDao.updateTask(updatedTask)

        // Cancelar alarma previa y programar nueva
        alarmScheduler?.cancelAlarm(task.id)
        alarmScheduler?.scheduleAlarm(updatedTask)

        // Detener efectos de alarma
        context?.let { ctx ->
            NotificationService.cancelCrisisNotification(ctx, task.id)
            AudioService.stopAudio()
            VibrationService.stopVibration(ctx)
        }

        return Result.success(updatedTask)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
        alarmScheduler?.cancelAlarm(task.id)
        context?.let { ctx ->
            NotificationService.cancelCrisisNotification(ctx, task.id)
        }
    }

    suspend fun deleteTaskById(taskId: Long) {
        taskDao.deleteTaskById(taskId)
        alarmScheduler?.cancelAlarm(taskId)
        context?.let { ctx ->
            NotificationService.cancelCrisisNotification(ctx, taskId)
        }
    }

    suspend fun clearCompletedTasks() {
        taskDao.clearCompletedTasks()
    }
}

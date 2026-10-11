package com.example.meeseekstaskmaster.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val deadline: Long,
    val status: TaskStatus = TaskStatus.PENDING,
    val isAlarmTriggered: Boolean = false,
    val completedAt: Long? = null
) {
    /**
     * Calcula la proporción de tiempo transcurrido (0.0 a 1.0) entre la creación y la fecha límite.
     * Controla divisiones por cero asegurando una duración mínima positiva.
     */
    fun getProgressRatio(currentTime: Long = System.currentTimeMillis()): Float {
        if (status == TaskStatus.COMPLETED) {
            return 1.0f
        }
        val totalDuration = (deadline - createdAt).coerceAtLeast(1000L)
        val elapsed = (currentTime - createdAt).coerceAtLeast(0L)
        return (elapsed.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
    }

    /**
     * Retorna el nivel de estrés correspondiente al momento actual.
     * El vencimiento tiene prioridad absoluta.
     */
    fun getStressLevel(currentTime: Long = System.currentTimeMillis()): MeeseeksStressLevel {
        val overdue = (status == TaskStatus.PENDING && currentTime >= deadline) || status == TaskStatus.OVERDUE
        return MeeseeksStressLevel.fromProgress(getProgressRatio(currentTime), overdue)
    }

    /**
     * Retorna los milisegundos restantes hasta la fecha límite.
     */
    fun getRemainingMillis(currentTime: Long = System.currentTimeMillis()): Long {
        return (deadline - currentTime).coerceAtLeast(0L)
    }

    /**
     * Comprueba si la tarea está vencida y pendiente.
     */
    fun isOverdue(currentTime: Long = System.currentTimeMillis()): Boolean {
        return (status == TaskStatus.PENDING && currentTime >= deadline) || status == TaskStatus.OVERDUE
    }
}

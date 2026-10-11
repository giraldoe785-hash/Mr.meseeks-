package com.example.meeseekstaskmaster.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {

    /**
     * Formatea el tiempo restante en formato estricto HH:MM:SS.
     * Si el tiempo es menor o igual a cero, retorna "00:00:00".
     */
    fun formatCountdown(remainingMillis: Long): String {
        if (remainingMillis <= 0) {
            return "00:00:00"
        }

        val totalSeconds = remainingMillis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }

    /**
     * Formatea una fecha y hora legible para el usuario (dd/MM/yyyy HH:mm).
     */
    fun formatDateTime(epochMillis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(epochMillis))
    }

    /**
     * Formatea solo la fecha (dd/MM/yyyy).
     */
    fun formatDate(epochMillis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(epochMillis))
    }

    /**
     * Formatea solo la hora (HH:mm).
     */
    fun formatTime(epochMillis: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(epochMillis))
    }

    /**
     * Calcula los milisegundos restantes hasta la fecha límite.
     */
    fun calculateRemainingMillis(deadline: Long, now: Long = System.currentTimeMillis()): Long {
        return (deadline - now).coerceAtLeast(0L)
    }

    /**
     * Determina si una tarea ha superado su fecha límite.
     */
    fun isOverdue(deadline: Long, now: Long = System.currentTimeMillis()): Boolean {
        return now >= deadline
    }

    /**
     * Formatea el tiempo transcurrido desde que la tarea venció.
     */
    fun formatOverdueElapsed(elapsedMillis: Long): String {
        val totalSeconds = (elapsedMillis / 1000).coerceAtLeast(0L)
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format(Locale.US, "+%02d:%02d:%02d", hours, minutes, seconds)
    }
}

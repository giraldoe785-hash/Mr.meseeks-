package com.example.meeseekstaskmaster.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

object VibrationService {
    private const val TAG = "VibrationService"
    private var isVibrating = false

    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    @Synchronized
    fun startVibration(context: Context) {
        if (isVibrating) return

        try {
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            // Patrón de vibración desesperado: espera 0ms, vibra 400ms, pausa 150ms, vibra 400ms, pausa 150ms, vibra 700ms
            val timings = longArrayOf(0, 400, 150, 400, 150, 700)
            val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(timings, amplitudes, 0) // Repetición continua
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(timings, 0)
            }
            isVibrating = true
        } catch (e: Exception) {
            Log.e(TAG, "Error al iniciar vibración: ${e.message}", e)
        }
    }

    @Synchronized
    fun stopVibration(context: Context) {
        try {
            val vibrator = getVibrator(context)
            vibrator?.cancel()
        } catch (e: Exception) {
            Log.e(TAG, "Error al detener vibración: ${e.message}", e)
        } finally {
            isVibrating = false
        }
    }

    fun isVibrating(): Boolean = isVibrating
}

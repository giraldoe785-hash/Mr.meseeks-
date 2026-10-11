package com.example.meeseekstaskmaster.viewmodel

import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.meeseekstaskmaster.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val soundEnabled: StateFlow<Boolean> = settingsRepository.soundEnabled
    val vibrationEnabled: StateFlow<Boolean> = settingsRepository.vibrationEnabled

    private val _hasNotificationPermission = MutableStateFlow(true)
    val hasNotificationPermission: StateFlow<Boolean> = _hasNotificationPermission.asStateFlow()

    private val _canScheduleExactAlarms = MutableStateFlow(true)
    val canScheduleExactAlarms: StateFlow<Boolean> = _canScheduleExactAlarms.asStateFlow()

    fun toggleSound(enabled: Boolean) {
        settingsRepository.setSoundEnabled(enabled)
    }

    fun toggleVibration(enabled: Boolean) {
        settingsRepository.setVibrationEnabled(enabled)
    }

    fun refreshPermissions(context: Context) {
        // Comprobar permiso de notificaciones (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            _hasNotificationPermission.value = granted
        } else {
            _hasNotificationPermission.value = true
        }

        // Comprobar permiso de alarma exacta (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            _canScheduleExactAlarms.value = alarmManager?.canScheduleExactAlarms() ?: true
        } else {
            _canScheduleExactAlarms.value = true
        }
    }
}

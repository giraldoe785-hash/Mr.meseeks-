package com.example.meeseekstaskmaster.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _soundEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND, true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(prefs.getBoolean(KEY_VIBRATION, true))
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
        _soundEnabled.value = enabled
    }

    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply()
        _vibrationEnabled.value = enabled
    }

    fun isSoundEnabledSync(): Boolean = prefs.getBoolean(KEY_SOUND, true)
    fun isVibrationEnabledSync(): Boolean = prefs.getBoolean(KEY_VIBRATION, true)

    companion object {
        private const val PREFS_NAME = "meeseeks_preferences"
        private const val KEY_SOUND = "pref_sound_enabled"
        private const val KEY_VIBRATION = "pref_vibration_enabled"

        @Volatile
        private var INSTANCE: SettingsRepository? = null

        fun getInstance(context: Context): SettingsRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingsRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}

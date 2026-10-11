package com.example.meeseekstaskmaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.meeseekstaskmaster.data.repository.SettingsRepository
import com.example.meeseekstaskmaster.data.repository.TaskRepository

class ViewModelFactory(
    private val taskRepository: TaskRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(taskRepository) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(taskRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(settingsRepository) as T
            }
            modelClass.isAssignableFrom(CrisisViewModel::class.java) -> {
                CrisisViewModel(taskRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

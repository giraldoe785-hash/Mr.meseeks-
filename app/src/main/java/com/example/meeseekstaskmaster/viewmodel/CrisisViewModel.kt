package com.example.meeseekstaskmaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meeseekstaskmaster.data.repository.TaskRepository
import com.example.meeseekstaskmaster.model.Task
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CrisisViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task.asStateFlow()

    private val _currentTime = MutableStateFlow(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                _currentTime.value = System.currentTimeMillis()
                delay(1000L)
            }
        }
    }

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            val loadedTask = taskRepository.getTaskById(taskId)
            _task.value = loadedTask
        }
    }

    fun completeAndKill(onFinished: () -> Unit) {
        val currentTask = _task.value ?: return
        if (_isProcessing.value) return

        _isProcessing.value = true
        viewModelScope.launch {
            taskRepository.completeTask(currentTask)
            _isProcessing.value = false
            onFinished()
        }
    }

    fun postponeTenMinutes(onFinished: () -> Unit) {
        val currentTask = _task.value ?: return
        if (_isProcessing.value) return

        _isProcessing.value = true
        viewModelScope.launch {
            taskRepository.postponeTask(currentTask.id, 10)
            _isProcessing.value = false
            onFinished()
        }
    }
}

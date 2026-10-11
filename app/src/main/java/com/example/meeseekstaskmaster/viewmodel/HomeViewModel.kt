package com.example.meeseekstaskmaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meeseekstaskmaster.data.repository.TaskRepository
import com.example.meeseekstaskmaster.model.Task
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    // Lista de tareas activas desde Room
    val activeTasks: StateFlow<List<Task>> = taskRepository.activeTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reloj en tiempo real para actualizar cuentas regresivas y niveles de estrés cada segundo
    private val _currentTime = MutableStateFlow(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime.asStateFlow()

    // Control del diálogo de creación
    private val _isCreateDialogOpen = MutableStateFlow(false)
    val isCreateDialogOpen: StateFlow<Boolean> = _isCreateDialogOpen.asStateFlow()

    // Mensajes para el usuario (Snackbar o feedback)
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        // Corrutina ticker para actualizar las cuentas regresivas cada segundo
        viewModelScope.launch {
            while (isActive) {
                _currentTime.value = System.currentTimeMillis()
                delay(1000L)
            }
        }
    }

    fun openCreateDialog() {
        _isCreateDialogOpen.value = true
    }

    fun closeCreateDialog() {
        _isCreateDialogOpen.value = false
    }

    fun createTask(title: String, description: String, deadline: Long) {
        viewModelScope.launch {
            val result = taskRepository.createTask(title, description, deadline)
            result.onSuccess {
                _isCreateDialogOpen.value = false
                _userMessage.value = "¡Tarea creada! Meeseeks convocado para servir."
            }.onFailure { error ->
                _userMessage.value = error.message ?: "Error al crear la tarea."
            }
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            taskRepository.completeTask(task)
            _userMessage.value = "¡POOF! Meeseeks cumplió su propósito y dejó de existir."
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            _userMessage.value = "Tarea eliminada."
        }
    }

    fun dismissMessage() {
        _userMessage.value = null
    }
}

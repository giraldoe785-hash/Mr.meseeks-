package com.example.meeseekstaskmaster.util

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

object TaskValidation {

    /**
     * Valida los datos requeridos para la creación o modificación de una tarea.
     * Retorna [ValidationResult.Valid] si cumple las reglas de negocio, o [ValidationResult.Error] con el motivo.
     */
    fun validate(
        title: String,
        deadline: Long,
        currentTime: Long = System.currentTimeMillis()
    ): ValidationResult {
        val trimmedTitle = title.trim()

        if (trimmedTitle.isEmpty()) {
            return ValidationResult.Error("El título de la tarea es obligatorio.")
        }

        if (trimmedTitle.length < 3) {
            return ValidationResult.Error("El título debe tener al menos 3 caracteres.")
        }

        if (deadline <= currentTime) {
            return ValidationResult.Error("La fecha y hora límite deben estar en el futuro.")
        }

        return ValidationResult.Valid
    }
}

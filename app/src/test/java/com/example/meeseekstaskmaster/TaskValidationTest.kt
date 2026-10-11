package com.example.meeseekstaskmaster

import com.example.meeseekstaskmaster.util.TaskValidation
import com.example.meeseekstaskmaster.util.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskValidationTest {

    private val currentTime = 1000000L

    @Test
    fun validate_emptyTitle_returnsError() {
        val result = TaskValidation.validate("", currentTime + 60000, currentTime)
        assertTrue(result is ValidationResult.Error)
        assertEquals("El título de la tarea es obligatorio.", (result as ValidationResult.Error).message)
    }

    @Test
    fun validate_whitespaceOnlyTitle_returnsError() {
        val result = TaskValidation.validate("   ", currentTime + 60000, currentTime)
        assertTrue(result is ValidationResult.Error)
        assertEquals("El título de la tarea es obligatorio.", (result as ValidationResult.Error).message)
    }

    @Test
    fun validate_shortTitle_returnsError() {
        val result = TaskValidation.validate("AB", currentTime + 60000, currentTime)
        assertTrue(result is ValidationResult.Error)
        assertEquals("El título debe tener al menos 3 caracteres.", (result as ValidationResult.Error).message)
    }

    @Test
    fun validate_pastDeadline_returnsError() {
        val result = TaskValidation.validate("Aprender Kotlin", currentTime - 1000, currentTime)
        assertTrue(result is ValidationResult.Error)
        assertEquals("La fecha y hora límite deben estar en el futuro.", (result as ValidationResult.Error).message)
    }

    @Test
    fun validate_presentDeadline_returnsError() {
        val result = TaskValidation.validate("Aprender Kotlin", currentTime, currentTime)
        assertTrue(result is ValidationResult.Error)
        assertEquals("La fecha y hora límite deben estar en el futuro.", (result as ValidationResult.Error).message)
    }

    @Test
    fun validate_validInputs_returnsValid() {
        val result = TaskValidation.validate("Aprender Kotlin Jetpack Compose", currentTime + 3600000, currentTime)
        assertTrue(result is ValidationResult.Valid)
    }
}

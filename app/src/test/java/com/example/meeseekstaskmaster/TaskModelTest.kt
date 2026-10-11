package com.example.meeseekstaskmaster

import com.example.meeseekstaskmaster.model.MeeseeksStressLevel
import com.example.meeseekstaskmaster.model.Task
import com.example.meeseekstaskmaster.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskModelTest {

    @Test
    fun getProgressRatio_calculatesElapsedCorrectly() {
        val created = 1000L
        val deadline = 5000L // Duración total = 4000L

        val task = Task(
            id = 1,
            title = "Test Task",
            createdAt = created,
            deadline = deadline
        )

        // En la mitad del tiempo: 1000 + 2000 = 3000L -> progreso 0.5f
        assertEquals(0.5f, task.getProgressRatio(3000L), 0.001f)

        // Al inicio: 1000L -> progreso 0.0f
        assertEquals(0.0f, task.getProgressRatio(1000L), 0.001f)

        // Al final: 5000L -> progreso 1.0f
        assertEquals(1.0f, task.getProgressRatio(5000L), 0.001f)
    }

    @Test
    fun getProgressRatio_zeroDuration_preventsDivisionByZero() {
        val task = Task(
            id = 2,
            title = "Zero Duration Task",
            createdAt = 1000L,
            deadline = 1000L
        )

        // Debe mantenerse acotado y no lanzar ArithmeticException
        val ratio = task.getProgressRatio(1000L)
        assertTrue(ratio in 0.0f..1.0f)
    }

    @Test
    fun getProgressRatio_completedTask_returnsOne() {
        val task = Task(
            id = 3,
            title = "Completed Task",
            createdAt = 1000L,
            deadline = 10000L,
            status = TaskStatus.COMPLETED
        )

        assertEquals(1.0f, task.getProgressRatio(2000L), 0.001f)
    }

    @Test
    fun getStressLevel_transitionsAtCorrectThresholds() {
        val created = 0L
        val deadline = 100L

        val task = Task(
            id = 4,
            title = "Stress Transition Task",
            createdAt = created,
            deadline = deadline
        )

        // 30% -> NORMAL
        assertEquals(MeeseeksStressLevel.NORMAL, task.getStressLevel(30L))

        // 60% -> STRESSED
        assertEquals(MeeseeksStressLevel.STRESSED, task.getStressLevel(60L))

        // 90% -> CRISIS
        assertEquals(MeeseeksStressLevel.CRISIS, task.getStressLevel(90L))

        // 100% -> OVERDUE
        assertEquals(MeeseeksStressLevel.OVERDUE, task.getStressLevel(100L))

        // 120% (Pasado) -> OVERDUE
        assertEquals(MeeseeksStressLevel.OVERDUE, task.getStressLevel(120L))
    }

    @Test
    fun isOverdue_returnsExpectedStatus() {
        val task = Task(
            id = 5,
            title = "Overdue Test",
            createdAt = 0L,
            deadline = 50L
        )

        assertFalse(task.isOverdue(40L))
        assertTrue(task.isOverdue(50L))
        assertTrue(task.isOverdue(60L))
    }
}

package com.example.meeseekstaskmaster

import com.example.meeseekstaskmaster.data.local.TaskDao
import com.example.meeseekstaskmaster.data.repository.TaskRepository
import com.example.meeseekstaskmaster.model.Task
import com.example.meeseekstaskmaster.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TaskRepositoryTest {

    private lateinit var fakeDao: FakeTaskDao
    private lateinit var repository: TaskRepository

    @Before
    fun setUp() {
        fakeDao = FakeTaskDao()
        repository = TaskRepository(fakeDao, null, null)
    }

    @Test
    fun createTask_validInputs_insertsAndReturnsTask() = runTest {
        val deadline = System.currentTimeMillis() + 3600000L
        val result = repository.createTask("Hacer la compra", "Comprar leche y cereal", deadline)

        assertTrue(result.isSuccess)
        val created = result.getOrNull()
        assertNotNull(created)
        assertEquals("Hacer la compra", created?.title)
        assertEquals(TaskStatus.PENDING, created?.status)
    }

    @Test
    fun createTask_invalidTitle_failsWithException() = runTest {
        val deadline = System.currentTimeMillis() + 3600000L
        val result = repository.createTask("", "Descripción", deadline)

        assertTrue(result.isFailure)
    }

    @Test
    fun completeTask_updatesStatusAndCompletedAt() = runTest {
        val task = Task(
            id = 10,
            title = "Limpiar la cochera",
            deadline = System.currentTimeMillis() + 10000L,
            status = TaskStatus.PENDING
        )
        fakeDao.insertTask(task)

        repository.completeTask(task)

        val updated = fakeDao.getTaskById(10)
        assertNotNull(updated)
        assertEquals(TaskStatus.COMPLETED, updated?.status)
        assertNotNull(updated?.completedAt)
    }

    @Test
    fun postponeTask_addsTenMinutesAndResetsPending() = runTest {
        val now = System.currentTimeMillis()
        val originalDeadline = now - 10000L // Tarea vencida
        val task = Task(
            id = 20,
            title = "Aprobar examen",
            deadline = originalDeadline,
            status = TaskStatus.OVERDUE,
            isAlarmTriggered = true
        )
        fakeDao.insertTask(task)

        val result = repository.postponeTask(20, 10)

        assertTrue(result.isSuccess)
        val postponed = result.getOrNull()
        assertNotNull(postponed)
        assertEquals(TaskStatus.PENDING, postponed?.status)
        assertEquals(false, postponed?.isAlarmTriggered)

        // El nuevo deadline debe ser aproximadamente ahora + 10 minutos (600,000 ms)
        val expectedMinDeadline = now + (9 * 60 * 1000L)
        assertTrue(postponed!!.deadline >= expectedMinDeadline)
    }

    // Fake DAO para pruebas en memoria
    private class FakeTaskDao : TaskDao {
        private val tasks = mutableMapOf<Long, Task>()
        private var nextId = 1L
        private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())

        override fun getAllTasksFlow(): Flow<List<Task>> = tasksFlow.asStateFlow()

        override fun getActiveTasksFlow(): Flow<List<Task>> = MutableStateFlow(
            tasks.values.filter { it.status == TaskStatus.PENDING }
        )

        override fun getHistoryTasksFlow(): Flow<List<Task>> = MutableStateFlow(
            tasks.values.filter { it.status != TaskStatus.PENDING }
        )

        override suspend fun getTaskById(id: Long): Task? = tasks[id]

        override fun getTaskByIdFlow(id: Long): Flow<Task?> = MutableStateFlow(tasks[id])

        override suspend fun getPendingTasks(): List<Task> =
            tasks.values.filter { it.status == TaskStatus.PENDING }

        override suspend fun insertTask(task: Task): Long {
            val id = if (task.id == 0L) nextId++ else task.id
            val stored = task.copy(id = id)
            tasks[id] = stored
            tasksFlow.value = tasks.values.toList()
            return id
        }

        override suspend fun updateTask(task: Task) {
            tasks[task.id] = task
            tasksFlow.value = tasks.values.toList()
        }

        override suspend fun deleteTask(task: Task) {
            tasks.remove(task.id)
            tasksFlow.value = tasks.values.toList()
        }

        override suspend fun deleteTaskById(id: Long) {
            tasks.remove(id)
            tasksFlow.value = tasks.values.toList()
        }

        override suspend fun clearCompletedTasks() {
            tasks.entries.removeIf { it.value.status == TaskStatus.COMPLETED }
            tasksFlow.value = tasks.values.toList()
        }
    }
}

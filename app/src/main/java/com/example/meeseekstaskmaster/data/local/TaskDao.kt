package com.example.meeseekstaskmaster.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.meeseekstaskmaster.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    fun getAllTasksFlow(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE status = 'PENDING' ORDER BY deadline ASC")
    fun getActiveTasksFlow(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE status != 'PENDING' ORDER BY COALESCE(completedAt, deadline) DESC")
    fun getHistoryTasksFlow(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskByIdFlow(id: Long): Flow<Task?>

    @Query("SELECT * FROM tasks WHERE status = 'PENDING'")
    suspend fun getPendingTasks(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("DELETE FROM tasks WHERE status = 'COMPLETED'")
    suspend fun clearCompletedTasks()
}

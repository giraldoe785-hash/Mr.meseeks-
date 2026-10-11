package com.example.meeseekstaskmaster.data.local

import androidx.room.TypeConverter
import com.example.meeseekstaskmaster.model.TaskStatus

class Converters {

    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String {
        return status.name
    }

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus {
        return try {
            TaskStatus.valueOf(value)
        } catch (e: Exception) {
            TaskStatus.PENDING
        }
    }
}

package com.example.meeseekstaskmaster.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.meeseekstaskmaster.model.Task
import com.example.meeseekstaskmaster.model.TaskStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Task::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "meeseeks_database"
                )
                    .addCallback(DatabasePrepopulateCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabasePrepopulateCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Se ejecuta única y exclusivamente en la creación inicial de la base de datos
                CoroutineScope(Dispatchers.IO).launch {
                    INSTANCE?.let { database ->
                        val now = System.currentTimeMillis()
                        val sampleTasks = listOf(
                            Task(
                                title = "Entregar el trabajo de programación",
                                description = "Subir repositorio y video explicativo de Android Kotlin con Compose a la plataforma universitaria.",
                                createdAt = now - (2 * 3600 * 1000L), // Creado hace 2 horas para mostrar estrés visible
                                deadline = now + (3 * 3600 * 1000L),  // Vence en 3 horas
                                status = TaskStatus.PENDING
                            ),
                            Task(
                                title = "Estudiar para el examen",
                                description = "Repasar ciclo de vida de actividades, corrutinas, Room y arquitectura MVVM.",
                                createdAt = now,
                                deadline = now + (24 * 3600 * 1000L), // Vence en 24 horas
                                status = TaskStatus.PENDING
                            )
                        )
                        for (task in sampleTasks) {
                            database.taskDao().insertTask(task)
                        }
                    }
                }
            }
        }
    }
}

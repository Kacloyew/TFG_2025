package com.example.tfg_2025.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tfg_2025.model.Libro

@Database(entities = [Libro::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun libroDao(): LibroDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        @Volatile
        private var currentUserId: String? = null

        fun getDatabase(context: Context, userId: String): AppDatabase {
            // Si cambia el usuario, destruir la instancia anterior
            if (currentUserId != userId) {
                INSTANCE?.close()
                INSTANCE = null
                currentUserId = userId
            }

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "libros_database_$userId"
                )
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()

                INSTANCE = instance
                instance
            }
        }

        // Llamar al hacer logout
        fun cerrarDatabase() {
            INSTANCE?.close()
            INSTANCE = null
            currentUserId = null
        }
    }
}
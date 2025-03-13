package com.example.doit.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.doit.database.converters.DateConverter
import com.example.doit.database.converters.TaskStatusConverter
import com.example.doit.models.TaskModel
import com.example.doit.models.TaskDao

@Database(
    entities = [TaskModel::class],
    version = 1
)
@TypeConverters(
    TaskStatusConverter::class,
    DateConverter::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getTaskDao(): TaskDao
}
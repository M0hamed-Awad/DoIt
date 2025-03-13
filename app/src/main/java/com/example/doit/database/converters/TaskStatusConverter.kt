package com.example.doit.database.converters

import androidx.room.TypeConverter
import com.example.doit.models.TaskStatus

class TaskStatusConverter {
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String {
        return status.status
    }

    @TypeConverter
    fun toTaskStatus(statusString: String): TaskStatus {
        return TaskStatus.entries.first { it.status == statusString }
    }
}
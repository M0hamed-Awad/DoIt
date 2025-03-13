package com.example.doit.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "tasks")
data class TaskModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var title: String,
    var description: String,
    var deadline: LocalDateTime,
    var taskStatus: TaskStatus = TaskStatus.IN_PROGRESS
)
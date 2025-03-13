package com.example.doit.models

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface TaskDao {
    @Upsert
    suspend fun insertTask(taskModel: TaskModel)

    @Delete
    suspend fun deleteTask(taskModel: TaskModel)

    @Query("SELECT * FROM tasks where taskStatus = 'In Progress'")
    fun getInProgressTasks(): LiveData<List<TaskModel>>

    @Query("SELECT * FROM tasks where taskStatus = 'Completed'")
    fun getCompletedTasks(): LiveData<List<TaskModel>>

    @Query("SELECT * FROM tasks where taskStatus = 'Overdue'")
    fun getOverdueTasks(): LiveData<List<TaskModel>>

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<TaskModel>
}
package com.example.doit.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.doit.models.TaskModel

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

    @Query("UPDATE tasks SET taskStatus = 'Overdue' WHERE deadline < :currentTime AND taskStatus == 'In Progress'")
    suspend fun updateOverdueTasks(currentTime: String)

    @Query("SELECT * FROM tasks WHERE LOWER(title) LIKE LOWER('%' || :text || '%') OR LOWER(description) LIKE LOWER('%' || :text || '%')")
    fun searchTasks(text: String): LiveData<List<TaskModel>>

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): LiveData<List<TaskModel>>
}
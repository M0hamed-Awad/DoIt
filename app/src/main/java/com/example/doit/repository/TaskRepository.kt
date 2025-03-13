package com.example.doit.repository

import androidx.lifecycle.LiveData
import com.example.doit.models.TaskModel
import com.example.doit.models.TaskDao

class TaskRepository(private val dao: TaskDao) {

    suspend fun insertTask(taskModel: TaskModel) {
        dao.insertTask(taskModel)
    }

    suspend fun deleteTask(taskModel: TaskModel) {
        dao.deleteTask(taskModel)
    }

    fun getInProgressTasks(): LiveData<List<TaskModel>> {
        return dao.getInProgressTasks()
    }

    fun getCompletedTasks(): LiveData<List<TaskModel>> {
        return dao.getCompletedTasks()
    }

    fun getOverdueTasks(): LiveData<List<TaskModel>> {
        return dao.getOverdueTasks()
    }
}
package com.example.doit.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.models.TaskModel
import com.example.doit.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val inProgressTasks: LiveData<List<TaskModel>> = repository.getInProgressTasks()
    val completedTasks: LiveData<List<TaskModel>> = repository.getCompletedTasks()
    val overdueTasks: LiveData<List<TaskModel>> = repository.getOverdueTasks()


    fun insertTask(taskModel: TaskModel) {
        viewModelScope.launch {
            repository.insertTask(taskModel)
        }
    }

    fun deleteTask(taskModel: TaskModel) {
        viewModelScope.launch {
            repository.deleteTask(taskModel)
        }
    }
}
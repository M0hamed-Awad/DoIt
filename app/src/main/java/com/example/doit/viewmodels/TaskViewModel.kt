package com.example.doit.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.models.TaskModel
import com.example.doit.repository.TaskRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    init {
        updateOverdueTasks()
    }

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

    private fun updateOverdueTasks() {
        viewModelScope.launch {
            val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            repository.updateOverdueTasks(now)
        }
    }

    fun searchTasks(query: String): LiveData<List<TaskModel>> {
        return repository.getSearchedTasks(query)
    }
}
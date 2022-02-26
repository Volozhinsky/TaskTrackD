package com.mithix.tasktrack

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskListViewModel:ViewModel() {
    private val taskRepository = TaskRepository.get()
    private val googleTaskRepository = GoogleTaskRepository.get()
    val taskListLiveData = taskRepository.getTasks()
    val googleTaskListLiveData =googleTaskRepository.getTasks()


    fun addTask(task: Task){
        taskRepository.addTask(task)
    }

}
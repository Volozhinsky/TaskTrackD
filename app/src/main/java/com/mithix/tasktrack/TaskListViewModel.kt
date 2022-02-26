package com.mithix.tasktrack

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.services.tasks.Tasks
import kotlinx.coroutines.launch

class TaskListViewModel(private val service: Tasks?):ViewModel() {
    private val taskRepository = TaskRepository.get()
    private val googleTaskRepository = GoogleTaskRepository.get()
    val taskListLiveData = taskRepository.getTasks()
    var googleTaskListLiveData =googleTaskRepository.getTasks(service)


    fun addTask(task: Task){
        taskRepository.addTask(task)
    }
    fun loadGooglelist(serviceN: Tasks?){
        googleTaskListLiveData = googleTaskRepository.getTasks(serviceN)
    }
}
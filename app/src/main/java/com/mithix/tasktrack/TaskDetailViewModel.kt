package com.mithix.tasktrack

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*
class TaskDetailViewModel() : ViewModel(){
    private val taskRepositoty = TaskRepository.get()
    private val taskIdLiveData = MutableLiveData<UUID>()
    var taskLiveData: LiveData<Task?> =
        Transformations.switchMap(taskIdLiveData){
            taskId -> taskRepositoty.getTask(taskId)
        }
    fun loadTask(taskId:UUID){
        taskIdLiveData.value = taskId
    }

    fun saveTask(task: Task){
        taskRepositoty.updateTask(task)
    }


}
package com.mithix.tasktrack

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.api.services.tasks.Tasks

class TaskListViewModelFactory(private val service:Tasks?):ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  TaskListViewModel(service) as T
    }
}
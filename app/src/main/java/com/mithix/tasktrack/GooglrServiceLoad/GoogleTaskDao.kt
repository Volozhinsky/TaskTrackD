package com.mithix.tasktrack.GooglrServiceLoad

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData

import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.model.Task

class GoogleTaskDao {
      fun getGoogleTasks(service: Tasks?): LiveData<List<Task>> {
          val googleTasksList:LiveData<List<Task>>  = liveData{
              service?.let { val data = service.tasks().list("@default").setFields("items/title").execute().getItems()
                  emit(data) }
              }
          return googleTasksList
          }

    }


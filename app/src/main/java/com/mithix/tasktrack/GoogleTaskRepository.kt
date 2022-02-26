package com.mithix.tasktrack

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.api.services.tasks.Tasks
import com.mithix.tasktrack.GooglrServiceLoad.GoogleTaskDao
import java.lang.IllegalStateException
import java.util.*
import com.google.api.services.tasks.model.Task

class GoogleTaskRepository private constructor(context: Context){

//        private val database: TaskDataBase = Room.databaseBuilder(
//            context.applicationContext,
//            TaskDataBase::class.java,
//            DATABASE_NAME
//        ).build()
//        private val taskDao = database.taskDao()
//
//        fun getTasks(): LiveData<List<Task>> = taskDao.getTasks()
//        fun getTask(id: UUID): LiveData<Task?> = taskDao.getTask(id)
//
//

    private val googleTaskDao = GoogleTaskDao()


    fun getTasks(service: Tasks?):LiveData<List<Task>> = googleTaskDao.getGoogleTasks(service)



    companion object{
            private var ginstanse:GoogleTaskRepository? = null
            fun initialize (context: Context){
                if (ginstanse == null){
                    ginstanse = GoogleTaskRepository(context)
                }
            }

            fun get():GoogleTaskRepository{
                return ginstanse?: throw IllegalStateException("Task repository must be initialized")
            }
        }
    }



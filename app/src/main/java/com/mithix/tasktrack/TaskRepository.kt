package com.mithix.tasktrack

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.mithix.tasktrack.database.TaskDataBase
import java.lang.IllegalStateException
import java.security.AccessControlContext
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "task-database"

class TaskRepository private constructor(context: Context){
    private val database:TaskDataBase = Room.databaseBuilder(
        context.applicationContext,
        TaskDataBase::class.java,
        DATABASE_NAME
    ).build()
    private val taskDao = database.taskDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getTasks():LiveData<List<Task>> = taskDao.getTasks()
    fun getTask(id:UUID):LiveData<Task?> = taskDao.getTask(id)

    fun updateTask(task:Task){
        executor.execute {
            taskDao.updateTask(task)
        }
    }

    fun addTask(task: Task){
        executor.execute {
            taskDao.addTask(task)
        }
    }
    companion object{
        private var INSTANSE:TaskRepository? = null
        fun initialize (context: Context){
            if (INSTANSE == null){
                INSTANSE = TaskRepository(context)
            }
        }

        fun get():TaskRepository{
            return INSTANSE?: throw IllegalStateException("Task repository must be initialized")
        }
    }
}
package com.mithix.tasktrack.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mithix.tasktrack.Task

@Database(entities = [Task::class], version = 1)
@TypeConverters(TaskTypeConverters::class)
abstract class TaskDataBase :RoomDatabase(){
    abstract fun taskDao():TaskDao

}
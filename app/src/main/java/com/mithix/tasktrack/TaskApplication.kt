package com.mithix.tasktrack

import android.app.Application

class TaskApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        TaskRepository.initialize(this)
        GoogleTaskRepository.initialize((this))
    }
}
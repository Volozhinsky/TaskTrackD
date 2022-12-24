package com.mithix.tasktrack


import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity() ,TaskListFragment.Callbacks {


    val settings = getPreferences(Context.MODE_PRIVATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
             val fragment = TaskListFragment.newInstance(settings)
             supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
            }
    }

    override fun onTaskSelected(taskId: UUID) {
        val fragment = TaskFragment.newInstance(taskId)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .addToBackStack(null).commit()
    }



}
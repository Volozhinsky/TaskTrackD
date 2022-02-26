package com.mithix.tasktrack

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.util.*
import androidx.lifecycle.Observer
//import java.util.*


private const val ARG_TASK_ID = "task_id"
class TaskFragment :Fragment(){
    private lateinit var  task:Task
    private lateinit var titleField:EditText
    private lateinit var dateField:EditText
    private lateinit var finishedCheckBox: CheckBox
    private val taskDetailViewModel:TaskDetailViewModel by lazy {
        ViewModelProvider(this).get(TaskDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        task = Task()
        val taskID: UUID = arguments?.getSerializable(ARG_TASK_ID) as UUID
        taskDetailViewModel.loadTask(taskID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskDetailViewModel.taskLiveData.observe(viewLifecycleOwner, Observer{
            task -> task?.let {
                this.task = task
                updateUI()
        }
        })
    }
    fun updateUI(){
        titleField.setText(task.title)
        dateField.setText(task.date.toString())
        finishedCheckBox.apply {
            isChecked = task.isFinished
            jumpDrawablesToCurrentState()
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task,container,false)
        titleField = view.findViewById(R.id.task_title) as EditText
        dateField = view.findViewById(R.id.task_date) as EditText
        dateField.apply {
            setText(task.date.toString())
        }
        finishedCheckBox = view.findViewById(R.id.task_finished) as CheckBox
        return view
    }

    override fun onStart() {
        super.onStart()
        val titleWatcher = object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                task.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }


        }
        titleField.addTextChangedListener(titleWatcher)
        finishedCheckBox.apply {
            setOnCheckedChangeListener{_, isChecked ->
                task.isFinished = isChecked
            }
        }
    }

    override fun onStop() {
        super.onStop()
        taskDetailViewModel.saveTask(task)
    }

    companion object{
        fun newInstance(taskID: UUID):TaskFragment{
            val args = Bundle().apply {
                putSerializable(ARG_TASK_ID,taskID)
            }
            return TaskFragment().apply {
                arguments = args
            }
        }
    }
}
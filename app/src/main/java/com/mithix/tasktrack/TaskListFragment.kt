package com.mithix.tasktrack

import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.TasksScopes
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

private const val TAG = "CrimeListFragment"
private const val PREF_ACCOUNT_NAME = "accountName"

class TaskListFragment(val settings: SharedPreferences):Fragment() {
    interface Callbacks {
        fun onTaskSelected(taskId:UUID)
    }

    val LOGGING_LEVEL = Level.OFF
    val httpTransport: HttpTransport = NetHttpTransport()
    val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
    var credential: GoogleAccountCredential? = null
    var service: Tasks? = null
    var chooseAccountlauncer: ActivityResultLauncher<String>? = null




    val viewModelFactory =TaskListViewModelFactory(service)

    private val taskListViewModel:TaskListViewModel by lazy{
        ViewModelProvider(this,viewModelFactory).get(TaskListViewModel::class.java)
    }
    private var callbacks :Callbacks? =null
    private lateinit var taskRecyclerView: RecyclerView
    private var adapter:TaskAdapter? =TaskAdapter(emptyList())

    private lateinit var thumbnailDownloader: ThumbnailDownloader<TaskHolder>






    companion object{
        fun newInstance(settings: SharedPreferences):TaskListFragment{
//            val args = Bundle().apply {
//                putSerializable(ARG_TASK_ID,service)
//            }
//            return TaskFragment().apply {
//                arguments = args
//



            return TaskListFragment(settings)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.getLogger("com.google.api.client").level = LOGGING_LEVEL
        credential =  GoogleAccountCredential.usingOAuth2(context, Collections.singleton(TasksScopes.TASKS))
        credential?.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null))
        credential?.let {
            chooseAccountlauncer =
                registerForActivityResult(ChooseAccountContract(it.newChooseAccountIntent())) {
                    if (it.resultCode == Activity.RESULT_OK && it.data != null && it.data?.extras != null) {
                        val accountName =
                            it.data?.extras?.getString(AccountManager.KEY_ACCOUNT_NAME)
                        accountName?.let {
                            credential?.setSelectedAccountName(it)
                            val editor = settings.edit()
                            editor.putString(PREF_ACCOUNT_NAME, accountName)
                            editor.commit()
                            // AsyncLoadTasks.run(this);
                        }
                        service =Tasks.Builder(httpTransport, jsonFactory, credential).build()
                        taskListViewModel.loadGooglelist(service)
                    }

                }

        }



        setHasOptionsMenu(true)
        val responseHandler =Handler(Looper.myLooper()!!)
        thumbnailDownloader = ThumbnailDownloader(responseHandler,
            {taskHolder,bitmap ->
                // управляет интерфейсом
            }
        )
        lifecycle.addObserver(thumbnailDownloader)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks =null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_list,container,false)
        taskRecyclerView = view.findViewById(R.id.task_recycler_view) as RecyclerView
        taskRecyclerView.layoutManager = LinearLayoutManager(context)
        taskRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskListViewModel.taskListLiveData.observe(
            viewLifecycleOwner,
            Observer {tasks ->
                    tasks?.let {
                        updateUI(tasks)
                    }


            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragvent_menu_list,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.new_task ->{
                val task = Task()
                taskListViewModel.addTask(task)
                callbacks?.onTaskSelected(task.id)
                true
            }
            R.id.ChooseAcc -> {
                chooseAccountlauncer?.launch("")


                true
            }
            else ->  super.onOptionsItemSelected(item)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(thumbnailDownloader)
    }

    private fun updateUI(tasks:List<Task>) {
        adapter = TaskAdapter(tasks)
        taskRecyclerView.adapter = adapter
    }

    private inner class TaskHolder(view:View):RecyclerView.ViewHolder(view),View.OnClickListener{
        private val titleTextView:TextView = itemView.findViewById(R.id.TaskTitle )
        private val dateTextView:TextView = itemView.findViewById(R.id.task_date)
        private lateinit var task:Task
        init {
            itemView.setOnClickListener(this)
        }
        fun bind(task:Task){
            this.task = task
            titleTextView.text = this.task.title
            dateTextView.text = this.task.date.toString()
        }

        override fun onClick(v: View?) {
            callbacks?.onTaskSelected(task.id)
        }
    }

    private inner class TaskAdapter(var tasks:List<Task>):RecyclerView.Adapter<TaskHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
            val view = layoutInflater.inflate(R.layout.list_item_task,parent,false)
            return TaskHolder(view)
        }

        override fun onBindViewHolder(holder: TaskHolder, position: Int) {
            val task = tasks[position]
            holder.bind(task)
        }

        override fun getItemCount() = tasks.size

    }
}
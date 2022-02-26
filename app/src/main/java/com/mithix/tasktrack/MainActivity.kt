package com.mithix.tasktrack

import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
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


private const val PREF_ACCOUNT_NAME = "accountName"


class MainActivity : AppCompatActivity() ,TaskListFragment.Callbacks {
    val LOGGING_LEVEL = Level.OFF
    val httpTransport: HttpTransport = NetHttpTransport()
    val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
    var credential: GoogleAccountCredential? = null
    var service: Tasks? = null
    var chooseAccountlauncer: ActivityResultLauncher<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //GoogleTaskApi
        //Google Accounts
        Logger.getLogger("com.google.api.client").level = LOGGING_LEVEL
        credential =  GoogleAccountCredential.usingOAuth2(this, Collections.singleton(TasksScopes.TASKS))
        val settings = getPreferences(Context.MODE_PRIVATE)

        credential?.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null))
        //Tasks client

        credential?.let {
            chooseAccountlauncer =
                registerForActivityResult(ChooseAccountContract(it.newChooseAccountIntent())) {
                    if (it.resultCode == Activity.RESULT_OK && it.data != null && it.data?.extras != null) {
                        val accountName =
                            it.data?.extras?.getString(AccountManager.KEY_ACCOUNT_NAME)
                        accountName?.let {
                            credential?.setSelectedAccountName(it)
                            val settings = getPreferences(Context.MODE_PRIVATE)
                            val editor = settings.edit()
                            editor.putString(PREF_ACCOUNT_NAME, accountName)
                            editor.commit()
                            // AsyncLoadTasks.run(this);
                        }
                        service =Tasks.Builder(httpTransport, jsonFactory, credential).build()

                    }

                }

        }



            setContentView(R.layout.activity_main)
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment == null) {
                val fragment = TaskListFragment.newInstance()
                supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment)
                    .commit()
            }

    }

    override fun onTaskSelected(taskId: UUID) {
        val fragment = TaskFragment.newInstance(taskId)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .addToBackStack(null).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.ChooseAcc -> {
                chooseAccountlauncer?.launch("")


                true
            }
            else ->  super.onOptionsItemSelected(item)

        }
    }
}
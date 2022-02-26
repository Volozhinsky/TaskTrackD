package com.mithix.tasktrack

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult

import androidx.activity.result.contract.ActivityResultContract
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

class ChooseAccountContract(val intent: Intent):ActivityResultContract<String,ActivityResult>() {
    override fun createIntent(context: Context, input: String?): Intent {
       return intent

    }

    override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult {
        return  ActivityResult(resultCode, intent)

    }

}
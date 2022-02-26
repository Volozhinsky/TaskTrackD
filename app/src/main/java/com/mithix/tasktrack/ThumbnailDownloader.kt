package com.mithix.tasktrack

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD = 0

class ThumbnailDownloader<in T> (private val responseHandler:Handler,
                                    private val onThumbnailDownloaded:(T,Bitmap) -> Unit ): HandlerThread(TAG),LifecycleObserver{
    private var hasQuit = false
    private lateinit var requestHandler:Handler


    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }
    fun queueThumbnail(target: T, url: String) {
        Log.i(TAG, "Got a URL: $url")
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD,target).sendToTarget()


    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setup(){
        start()
        looper
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun tearDown(){
       quit()
    }

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        requestHandler = object :Handler(Looper.myLooper()!!){
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (msg.what == MESSAGE_DOWNLOAD){
                    val target = msg.obj as T

                    handleRequest(target)
                }
            }
        }
    }

    private fun handleRequest(target: T) {
        responseHandler.post(Runnable {
            if (!hasQuit) {
                return@Runnable
            }

        })

    }


}


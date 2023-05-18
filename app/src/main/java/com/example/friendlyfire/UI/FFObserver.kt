package com.example.friendlyfire.UI

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

class FFObserver (private val context: Context, private val lifecycle: Lifecycle) :
    DefaultLifecycleObserver {

    private val TAG = "FF_Observer"

    override fun onCreate(owner: LifecycleOwner){
        Log.i(TAG, "ON_CREATE -> $owner")
    }

    override fun onStart(owner: LifecycleOwner){
        Log.i(TAG, "ON_START -> $owner")
    }

    override fun onResume(owner: LifecycleOwner){
        Toast.makeText(context, "Retrieving data...", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "ON_RESUME -> $owner")
    }

    override fun onPause(owner: LifecycleOwner){
        Log.i(TAG, "ON_PAUSE -> $owner")
    }

    override fun onStop(owner: LifecycleOwner){
        Toast.makeText(context, "Switching Screens...", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "ON_STOP -> $owner")
    }

    override fun onDestroy(owner: LifecycleOwner){
        Log.i(TAG, "ON_DESTROY")
    }
}
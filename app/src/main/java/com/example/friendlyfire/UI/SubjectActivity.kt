package com.example.friendlyfire.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.friendlyfire.Model.ConnectionLiveData
import com.example.friendlyfire.R
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class SubjectActivity : AppCompatActivity() {
    private lateinit var groupRef : DocumentReference
    private val TAG = "SubjectActivity"

    private lateinit var liveData : ConnectionLiveData
    private lateinit var subjectLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)

        checkNetworkConnection()

        subjectLayout = findViewById(R.id.subjectLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

        val joinCode = intent.getStringExtra("join_code").toString()

        groupRef = FirebaseFirestore.getInstance().collection("Group").document(joinCode)
        subscribeToEndGame()
    }

    private fun subscribeToEndGame() {
        groupRef.addSnapshotListener { query, error ->
            Log.i(TAG, "Update detected...")
            error?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            query?.let {
                val canProgress = query["canProgress"] as Boolean
                Log.i(TAG, "Group Snapshot: ${query.id} => ${query.data}")
                if(canProgress){
                    Log.i(TAG, "Sending to Subject result screen with betDescription ${query["bet"].toString()}...")
                    val subjectResultActivity = Intent(this@SubjectActivity,SubjectResultActivity::class.java)
                    subjectResultActivity.putExtra("bet_description", query["bet"].toString())
                    startActivity(subjectResultActivity)
                    this.finish()
                }
            }
        }
    }
    private fun checkNetworkConnection(){
        liveData = ConnectionLiveData(application)
        liveData.observe(this) { isConnected ->

            if (isConnected) {
                subjectLayout.visibility = View.VISIBLE
                noWifiLayout.visibility = View.GONE

            } else {
                subjectLayout.visibility = View.GONE
                noWifiLayout.visibility = View.VISIBLE
            }
        }
    }
}
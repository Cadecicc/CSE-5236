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
import com.google.firebase.firestore.FirebaseFirestore

class YouAreBettorActivity : AppCompatActivity() {
    private lateinit var liveData : ConnectionLiveData
    private lateinit var youAreBetterLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_you_are_better)

        checkNetworkConnection()

        youAreBetterLayout = findViewById(R.id.youAreBetterLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

        val userId = intent.getStringExtra("user_id").toString()
        val emailId = intent.getStringExtra("email_id").toString()
        val joinCode = intent.getStringExtra("join_code").toString()
        val userRole = intent.getStringExtra("user_role").toString()
        val TAG = "YouAreBettorActivity"

        val listenerReg = FirebaseFirestore.getInstance()

        listenerReg.collection("Group").document(joinCode).addSnapshotListener { query, error ->
            Log.i(TAG, "Update detected...")
            error?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            query?.let {
                val betDescription = query["bet"].toString()
                Log.i(TAG, "User Snapshot: ${query.id} => ${query.data}")
                if(betDescription != "N/A"){
                    Log.i(TAG, "Sending to screen for betting on ${betDescription}...")
                    val bettorActivity = Intent(this@YouAreBettorActivity,BettorActivity::class.java)
                    bettorActivity.putExtra("user_id", userId)
                    bettorActivity.putExtra("email_id", emailId)
                    bettorActivity.putExtra("join_code", joinCode)
                    bettorActivity.putExtra("user_role", userRole)
                    bettorActivity.putExtra("bet_description", betDescription)
                    bettorActivity.putExtra("bet_value", query["bettingLine"] as Double)
                    bettorActivity.putExtra("tally_count", 2)

                    Log.i(TAG, "Sending $emailId...")
                    Log.i(TAG, "Sending $joinCode...")

                    listenerReg.terminate()
                    startActivity(bettorActivity)
                    this.finish()
                }
            }
        }

    }
    private fun checkNetworkConnection(){
        liveData = ConnectionLiveData(application)
        liveData.observe(this) { isConnected ->

            if (isConnected) {
                youAreBetterLayout.visibility = View.VISIBLE
                noWifiLayout.visibility = View.GONE

            } else {
                youAreBetterLayout.visibility = View.GONE
                noWifiLayout.visibility = View.VISIBLE
            }
        }
    }
}
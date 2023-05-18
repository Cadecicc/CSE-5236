package com.example.friendlyfire.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.example.friendlyfire.Model.ConnectionLiveData
import com.example.friendlyfire.R
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class LosingSideActivity : AppCompatActivity() {
    private lateinit var players : CollectionReference
    lateinit var group : DocumentReference
    private val TAG = "LosingSideActivity"

    private lateinit var liveData : ConnectionLiveData
    private lateinit var losingSideLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_losing_side)

        checkNetworkConnection()

        losingSideLayout = findViewById(R.id.losingSideLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

        val losingSide = intent.getStringExtra("losing_side").toString()
        val joinCode = intent.getStringExtra("join_code").toString()
        var playersInGroup : MutableList<String>
        var losses : Double

        val lossesTV = findViewById<TextView>(R.id.losses)

        players = FirebaseFirestore.getInstance().collection("Player")
        group = FirebaseFirestore.getInstance().collection("Group").document(joinCode)

        group.addSnapshotListener { query, error ->
            Log.i(TAG, "Update detected...")
            error?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            query.let {
                playersInGroup = query?.get("Players") as MutableList<String>

                var sumOfWagers = 0.0
                var sumOfSamePlayers = 0.0
                players.get().addOnSuccessListener { result ->
                    for (doc in result){
                        if(playersInGroup.contains(doc.id)){
                            if(doc.data["wagerSide"].toString() == losingSide && doc.data["role"] == "Bettor"){
                                sumOfWagers += doc.data["wagerAmount"].toString().toDouble()
                                sumOfSamePlayers += 1.0
                            }
                        }
                    }
                }.addOnSuccessListener {
                    losses = sumOfWagers / sumOfSamePlayers
                    lossesTV.text = getString(R.string.losingAmount, losses)
                }
            }
        }

        //venmo logic will go here


        val backToMenu = findViewById<Button>(R.id.backToMenuButton)
        backToMenu.setOnClickListener{
            val mainMenu = Intent(this@LosingSideActivity, MainMenuActivity::class.java)
            startActivity(mainMenu)
//            group.delete()
        }
    }
    private fun checkNetworkConnection(){
        liveData = ConnectionLiveData(application)
        liveData.observe(this) { isConnected ->

            if (isConnected) {
                losingSideLayout.visibility = View.VISIBLE
                noWifiLayout.visibility = View.GONE

            } else {
                losingSideLayout.visibility = View.GONE
                noWifiLayout.visibility = View.VISIBLE
            }
        }
    }
}
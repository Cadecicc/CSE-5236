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

class WinnerSideActivity : AppCompatActivity() {
    private lateinit var players : CollectionReference
    lateinit var group : DocumentReference
    private val TAG = "WinnerSideActivity"

    private lateinit var liveData : ConnectionLiveData
    private lateinit var winningSideLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_winner_side)

        checkNetworkConnection()

        winningSideLayout = findViewById(R.id.winningSideLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

        //use this to add up the total bets
//        val inputedBet = intent.getStringExtra("inputedBet")

        val winningSide = intent.getStringExtra("winning_side").toString()
        val joinCode = intent.getStringExtra("join_code").toString()
        var playersInGroup : MutableList<String>
        var winnings : Double

        val winningsTV = findViewById<TextView>(R.id.winnings)

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
                Log.i(TAG, "Players in Group => $playersInGroup")

                var sumOfWagers = 0.0
                var sumOfOpposingPlayers = 0.0
                players.get().addOnSuccessListener { result ->
                    for (doc in result){
                        Log.i(TAG, "${doc.id} => ${doc.data}")
                        if(playersInGroup.contains(doc.id)){
                            Log.i(TAG, "Scanning ${doc.id}'s information...")
                            if(doc.data["wagerSide"].toString() != winningSide && doc.data["role"] == "Bettor"){
                                sumOfWagers += doc.data["wagerAmount"].toString().toDouble()
                                sumOfOpposingPlayers += 1.0
                            }
                        }
                    }
                }.addOnSuccessListener {
                    Log.i(TAG, "Total Wagers: $sumOfWagers")
                    Log.i(TAG, "Total Players: $sumOfOpposingPlayers")

                    winnings = if (sumOfOpposingPlayers > 0.0) sumOfWagers / sumOfOpposingPlayers else 0.0
                    Log.i(TAG, "Winnings: $winnings")
                    winningsTV.text = getString(R.string.winningAmount, winnings)
                }
            }
        }

        //venmo logic will go here


        val backToMenu = findViewById<Button>(R.id.backToMenuButton)
        backToMenu.setOnClickListener {
            val mainMenu = Intent(this@WinnerSideActivity, MainMenuActivity::class.java)
            startActivity(mainMenu)
//            group.delete()
        }
    }
    private fun checkNetworkConnection(){
        liveData = ConnectionLiveData(application)
        liveData.observe(this) { isConnected ->

            if (isConnected) {
                winningSideLayout.visibility = View.VISIBLE
                noWifiLayout.visibility = View.GONE

            } else {
                winningSideLayout.visibility = View.GONE
                noWifiLayout.visibility = View.VISIBLE
            }
        }
    }
}
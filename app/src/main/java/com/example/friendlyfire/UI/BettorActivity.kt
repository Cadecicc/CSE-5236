package com.example.friendlyfire.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.example.friendlyfire.Model.ConnectionLiveData
import com.example.friendlyfire.R
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class BettorActivity : AppCompatActivity() {
    private lateinit var groupRef : DocumentReference
    private lateinit var playerRef : DocumentReference
    private val TAG = "BettorActivity"
    private var wagerSide = ""
    var joinCode = ""

    private lateinit var liveData : ConnectionLiveData
    private lateinit var bettorLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bettor)

        checkNetworkConnection()

        bettorLayout = findViewById(R.id.bettorLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

        val emailId = intent.getStringExtra("email_id").toString()
        joinCode = intent.getStringExtra("join_code").toString()
        groupRef = FirebaseFirestore.getInstance().collection("Group").document(joinCode)
        playerRef = FirebaseFirestore.getInstance().collection("Player").document(emailId)
        Log.i(TAG, "Set player reference to $emailId.")
        Log.i(TAG, "Set group reference to $joinCode.")


        val description = intent.getStringExtra("bet_description")
        val betLineValue = intent.getDoubleExtra("bet_value", 3.0)
        val descriptionView = findViewById<TextView>(R.id.bettingLineDescription)
        val numberView = findViewById<TextView>(R.id.bettingLineNumber)

        descriptionView.text = description.toString()
        numberView.text = betLineValue.toString()

        val playerBet = findViewById<EditText>(R.id.bet)
        val higherButton = findViewById<Button>(R.id.higherButton)
        val lowerButton = findViewById<Button>(R.id.lowerButton)

//        resetCanProgress()
        subscribeToEndGame()
        //we need tp compare tally count and betting line values as ints, and they were sent over as "String?"
//        val tallyCountInt = tallyCount.toString().toInt()
//        val betLineValueInt = betLineValue.toString().toInt()

        lowerButton.setOnClickListener {
            when {
                !TextUtils.isDigitsOnly(playerBet.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@BettorActivity,
                        "Your bet value must be an integer",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                else -> {
                    Log.i(TAG, "under bet being placed...")
                    wagerSide = "under"
                    setWagerData("under", playerBet.text.toString().toInt())
                    lowerButton.isEnabled = false
                    higherButton.isEnabled = false
                }
            }
        }

        //Same lines of code from 36-59, with flipped if statement logic
        higherButton.setOnClickListener {
            when {
                !TextUtils.isDigitsOnly(playerBet.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@BettorActivity,
                        "Your bet value must be an integer",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                else -> {
                    Log.i(TAG, "over bet being placed...")
                    wagerSide = "over"
                    setWagerData("over", playerBet.text.toString().toInt())
                    higherButton.isEnabled = false
                    lowerButton.isEnabled = false
                }
            }
        }

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
                val betLine = query["bettingLine"] as Double
                val tally = query["tally"] as Double
                Log.i(TAG, "Group Snapshot: ${query.id} => ${query.data}")
                if(canProgress){
                    Log.i(TAG, "wagerSide: $wagerSide\nbetLine: $betLine\ntally: $tally")
                    if(wagerSide == "under" && betLine > tally || wagerSide == "over" && betLine < tally){
                        Log.i(TAG, "Sending to winning screen...")
                        val bettorActivity = Intent(this@BettorActivity,WinnerSideActivity::class.java)
                        bettorActivity.putExtra("bet_description", query["bet"].toString())
                        bettorActivity.putExtra("bet_value", query["bettingLine"].toString())
                        bettorActivity.putExtra("tally_count", tally)
                        bettorActivity.putExtra("winning_side", wagerSide)
                        bettorActivity.putExtra("join_code", joinCode)
                        startActivity(bettorActivity)
                        this.finish()
                    }else {
                        Log.i(TAG, "Sending to losing screen...")
                        val bettorActivity = Intent(this@BettorActivity,LosingSideActivity::class.java)
                        bettorActivity.putExtra("bet_description", query["bet"].toString())
                        bettorActivity.putExtra("bet_value", query["bettingLine"].toString())
                        bettorActivity.putExtra("tally_count", tally)
                        bettorActivity.putExtra("losing_side", wagerSide)
                        bettorActivity.putExtra("join_code", joinCode)
                        startActivity(bettorActivity)
                        this.finish()
                    }
                }
            }
        }
    }

    private fun setWagerData(wagerSide: String, betValue: Number){
        Log.i(TAG, "Updating player data with wagerSide $wagerSide and bet value $betValue...")
        val playerData = mutableMapOf<String, Any>()
        playerData["wagerSide"] = wagerSide
        playerData["wagerAmount"] = betValue
        playerRef.set(
            playerData,
            SetOptions.merge()
        ).addOnFailureListener{exception ->
            Log.w(TAG, "Error updating player bet.", exception)
        }
    }
    private fun checkNetworkConnection(){
        liveData = ConnectionLiveData(application)
        liveData.observe(this) { isConnected ->

            if (isConnected) {
                bettorLayout.visibility = View.VISIBLE
                noWifiLayout.visibility = View.GONE

            } else {
                bettorLayout.visibility = View.GONE
                noWifiLayout.visibility = View.VISIBLE
            }
        }
    }
}

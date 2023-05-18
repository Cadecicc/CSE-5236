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
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class BookieTallyActivity : AppCompatActivity() {
    private lateinit var groupRef : DocumentReference
    private lateinit var liveData : ConnectionLiveData
    private lateinit var bookieTallyLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookie_tally)

        checkNetworkConnection()

        bookieTallyLayout = findViewById(R.id.bookieTallyLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

        val betLine = intent.getDoubleExtra("bet_line", 3.0)
        val joinCode = intent.getStringExtra("join_code").toString()
        groupRef = FirebaseFirestore.getInstance().collection("Group").document(joinCode)

        val minusButton = findViewById<Button>(R.id.minusButton)
        val plusButton = findViewById<Button>(R.id.plusButton)
        val resetButton = findViewById<Button>(R.id.resetButton)
        val endButton = findViewById<Button>(R.id.EndButton)
        val textView = findViewById(R.id.count) as TextView
        var num = 0.0

        minusButton.setOnClickListener{
            num -= 1.0
            textView.text = num.toString()

        }
        plusButton.setOnClickListener{
            num += 1.0
            textView.text = num.toString()

        }
        resetButton.setOnClickListener{
            num = 0.0
            textView.text = num.toString()
        }

        endButton.setOnClickListener{
            val data = mutableMapOf<String, Any>()
            data["tally"] = num
            data["canProgress"] = true

            groupRef.set(
                data,
                SetOptions.merge()
            ).addOnSuccessListener {
                Toast.makeText(
                    this@BookieTallyActivity,
                    "Successfully updated group tally data.",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    this@BookieTallyActivity,
                    exception.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            val intent = Intent(this@BookieTallyActivity, BookieResultsActivity::class.java)
            val tallyCount = num
            Log.i("BookieTallyActivity", "Total count: $tallyCount\nBet Line: $betLine")
            intent.putExtra("tally", tallyCount)
            intent.putExtra("bet_line", betLine)
            startActivity(intent)

//            val bettorActivity = Intent(this@BookieTallyActivity, BettorActivity::class.java)
//            bettorActivity.putExtra("tallyCount", num)
//
//            val bookieResults = Intent(this@BookieTallyActivity, BookieResultsActivity::class.java)
//            bookieResults.putExtra("tallyCount",num)
        }
    }
    private fun checkNetworkConnection(){
        liveData = ConnectionLiveData(application)
        liveData.observe(this) { isConnected ->

            if (isConnected) {
                bookieTallyLayout.visibility = View.VISIBLE
                noWifiLayout.visibility = View.GONE

            } else {
                bookieTallyLayout.visibility = View.GONE
                noWifiLayout.visibility = View.VISIBLE
            }
        }
    }
}
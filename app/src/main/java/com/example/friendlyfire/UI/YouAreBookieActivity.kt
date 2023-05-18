package com.example.friendlyfire.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.friendlyfire.Model.ConnectionLiveData
import com.example.friendlyfire.R
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class YouAreBookieActivity : AppCompatActivity() {
    private lateinit var groupRef : DocumentReference
    private lateinit var liveData : ConnectionLiveData
    private lateinit var youAreBookieLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_you_are_bookie)

        checkNetworkConnection()

        youAreBookieLayout = findViewById(R.id.youAreBookieLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

        val joinCode = intent.getStringExtra("join_code").toString()
        val betDescription = findViewById<EditText>(R.id.bettingLineDescription)
        val betLineValue = findViewById<EditText>(R.id.bettingLineNumber)
        val submitButton = findViewById<Button>(R.id.submitButton)
        groupRef = FirebaseFirestore.getInstance().collection("Group").document(joinCode)

        submitButton.setOnClickListener {
            when {
                //when the register button is pressed, check to see if the email or password is empty
                TextUtils.isEmpty(betDescription.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@YouAreBookieActivity,
                        "Please enter a bet description",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(betLineValue.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@YouAreBookieActivity,
                        "Please enter a bet value",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                !TextUtils.isDigitsOnly(betLineValue.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@YouAreBookieActivity,
                        "Your bet value must be an integer",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val betDescription: String = betDescription.text.toString().trim { it <= ' ' }
                    val betLineValue = betLineValue.text.toString().toDouble()
                    //Need a way to send this information to Better Activity
                    val betData = mutableMapOf<String, Any>()
                    betData["bet"] = betDescription
                    betData["bettingLine"] = betLineValue

                    groupRef.set(
                        betData,
                        SetOptions.merge()
                    ).addOnSuccessListener {
                        Toast.makeText(
                            this@YouAreBookieActivity,
                        "Successfully added wager data.",
                        Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener {
                        Toast.makeText(
                            this@YouAreBookieActivity,
                            it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    val bookieTally = Intent(this@YouAreBookieActivity, BookieTallyActivity::class.java)
                    bookieTally.putExtra("join_code", joinCode)
                    bookieTally.putExtra("bet_line", betLineValue)
                    startActivity(bookieTally)


                }

            }
        }
    }
    private fun checkNetworkConnection(){
        liveData = ConnectionLiveData(application)
        liveData.observe(this) { isConnected ->

            if (isConnected) {
                youAreBookieLayout.visibility = View.VISIBLE
                noWifiLayout.visibility = View.GONE

            } else {
                youAreBookieLayout.visibility = View.GONE
                noWifiLayout.visibility = View.VISIBLE
            }
        }
    }
}
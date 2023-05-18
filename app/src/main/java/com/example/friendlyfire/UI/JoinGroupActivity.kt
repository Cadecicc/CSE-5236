package com.example.friendlyfire.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.friendlyfire.Model.ConnectionLiveData
import com.example.friendlyfire.R
import com.google.firebase.firestore.*

class JoinGroupActivity : AppCompatActivity() {
    private lateinit var db : CollectionReference
    private var TAG : String = "JoinGroupActivity"

    private lateinit var liveData : ConnectionLiveData
    private lateinit var joinGroupLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_group)

        checkNetworkConnection()

        joinGroupLayout = findViewById(R.id.joinGroupLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

        val userId = intent.getStringExtra("user_id")
        val emailId = intent.getStringExtra("email_id")

        db = FirebaseFirestore.getInstance().collection("Group")

        val joinCode = findViewById<EditText>(R.id.enterCodeEdit)
        val joinButton = findViewById<Button>(R.id.joinButton)

        joinButton.setOnClickListener{
            val joinCodeText:String = joinCode.text.toString()
            val docRef:DocumentReference = db.document(joinCode.text.toString())
            db.get().addOnSuccessListener { result ->
                for(doc in result){
                    Log.i(TAG,"Comparing $joinCodeText to ${doc.id}...")

                    //if matching group is found
                    if(doc.id == joinCodeText){

                        //update Players with the new user
                        Log.i(TAG, "Adding $emailId to Group...")
                        Log.d(TAG, "${doc.id} => ${doc.data}")
                        val data:MutableMap<String, Any> = doc.data
                        Log.d(TAG, "${data["Players"]}")
                        val players: MutableList<Any?>? = data["Players"] as MutableList<Any?>?
                        players?.add("$emailId")
                        val map = mutableMapOf<String, Any?>()
                        map["Players"] = players

                        //send the new Players list up to firebase
                        docRef.set(
                            map,
                            SetOptions.merge()
                        ).addOnCompleteListener{ task ->
                            if(task.isSuccessful){
                                Toast.makeText(
                                    this@JoinGroupActivity,
                                    "Added to Group",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.i(TAG, "emailID is $emailId")
                                val intent = Intent(this@JoinGroupActivity, GroupLobbyActivity::class.java)
                                intent.putExtra("user_id", userId)
                                intent.putExtra("email_id", emailId)
                                intent.putExtra("join_code", joinCodeText)
                                startActivity(intent)
                                this.finish()
                            }else{
                                Toast.makeText(
                                    this@JoinGroupActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }.addOnFailureListener{ exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
        }

        val backButton = findViewById<Button>(R.id.backToMenuButton_)
        backButton.setOnClickListener {
            val intent = Intent(this@JoinGroupActivity, MainMenuActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("email_id", emailId)
            startActivity(intent)
            finish()
        }
    }
    private fun checkNetworkConnection(){
        liveData = ConnectionLiveData(application)
        liveData.observe(this) { isConnected ->

            if (isConnected) {
                joinGroupLayout.visibility = View.VISIBLE
                noWifiLayout.visibility = View.GONE

            } else {
                joinGroupLayout.visibility = View.GONE
                noWifiLayout.visibility = View.VISIBLE
            }
        }
    }
}
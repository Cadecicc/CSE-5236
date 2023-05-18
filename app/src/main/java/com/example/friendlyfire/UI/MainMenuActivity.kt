package com.example.friendlyfire.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.friendlyfire.Model.ConnectionLiveData
import com.example.friendlyfire.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class MainMenuActivity : AppCompatActivity() {
    private lateinit var db: CollectionReference
    lateinit var firestore : FirebaseFirestore

    private lateinit var liveData : ConnectionLiveData
    private lateinit var mainMenuLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        checkNetworkConnection()

        mainMenuLayout = findViewById(R.id.mainMenuLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

        //From register activity. User for the future
        val userId = intent.getStringExtra("user_id")
        val emailID = intent.getStringExtra("email_id")

        Log.i("MainMenuActivity", "userId: $userId\nemail_id: $emailID")

        firestore = FirebaseFirestore.getInstance()
        db = firestore.collection("Group")

        val makeGroupButton = findViewById<Button>(R.id.makeGroupButton)

        makeGroupButton.setOnClickListener {
            Log.i("MainMenuActivity", "Attempting to create group...")
            val items = HashMap<String, Any>()

            items["bet"] = "N/A"
            items["canProgress"] = false
            items["bettingLine"] = 0
            val players: MutableList<String> = mutableListOf(emailID.toString())
            items["Players"] = players
//            items.put("overPot", 0)
//            items.put("underPot", 0)
            items["tally"] = 0.0

            val joinCode:String = getJoinCode()
            db.document(joinCode).set(items).addOnCompleteListener { task ->
                Log.i("MainMenuActivity", "Entering makeGroup callback...")
                if(task.isSuccessful){
                    Toast.makeText(
                        this@MainMenuActivity,
                        "Group Created Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@MainMenuActivity, GroupLobbyActivity::class.java)
                    intent.putExtra("user_id", userId)
                    intent.putExtra("email_id", emailID)
                    intent.putExtra("join_code", joinCode)
                    startActivity(intent)
                    this.finish()
                } else {
                    Toast.makeText(
                        this@MainMenuActivity,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val joinGroupButton = findViewById<Button>(R.id.joinGroupButton)

        joinGroupButton.setOnClickListener {
            Log.i("MainMenuActivity", "emailID is $emailID")
            val intent = Intent(this@MainMenuActivity, JoinGroupActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("email_id", emailID)
            startActivity(intent)
            this.finish()
        }

        val logoutButton = findViewById<Button>(R.id.LogOut)

        logoutButton.setOnClickListener{

            //Logout from app
            FirebaseAuth.getInstance().signOut()

            //start home screen activity and send the user to it
            startActivity(Intent(this@MainMenuActivity, HomeScreenActivity::class.java))
            this.finish() //close the main menu since they are logging out
        }

        val profileButton = findViewById<ImageView>(R.id.profileButton)
        profileButton.setOnClickListener{
            //Send the user to the main menu
            val intent = Intent(this@MainMenuActivity, ProfilePageActivity::class.java)

            //remove instances of activities running in the background (login or register)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() //closes current activity. Otherwise runs in the background when we don't need it,
            //if user presses back, app will close
        }

        val deleteButton = findViewById<Button>(R.id.deleteButton)
        deleteButton.setOnClickListener{
            val user = FirebaseAuth.getInstance().currentUser
            user?.delete()?.addOnCompleteListener{task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@MainMenuActivity,
                        "Account Deleted Successfully!",
                        Toast.LENGTH_SHORT
                    ).show()

                    //send user back to home screen
                    val intent = Intent(this@MainMenuActivity, HomeScreenActivity::class.java)
                    startActivity(intent)
                    finish() //no longer need this activity instance.
                }
                else{
                    //Email doesn't exist in the system or some other error
                    Toast.makeText(
                        this@MainMenuActivity,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }
    private fun checkNetworkConnection(){
        liveData = ConnectionLiveData(application)
        liveData.observe(this) { isConnected ->

            if (isConnected) {
                mainMenuLayout.visibility = View.VISIBLE
                noWifiLayout.visibility = View.GONE

            } else {
                mainMenuLayout.visibility = View.GONE
                noWifiLayout.visibility = View.VISIBLE
            }
        }
    }
}

fun getJoinCode() : String {
    val length = 7
    val possibleChars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..length).map{ possibleChars.random() }.joinToString(separator = "")
}
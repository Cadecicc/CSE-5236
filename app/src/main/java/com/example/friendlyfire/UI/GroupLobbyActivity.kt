package com.example.friendlyfire.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.friendlyfire.Model.ConnectionLiveData
import com.example.friendlyfire.R
import com.google.firebase.firestore.*

class GroupLobbyActivity : AppCompatActivity() {

    private var dbGroup : CollectionReference = FirebaseFirestore.getInstance().collection("Group")
    private var dbPlayer: CollectionReference = FirebaseFirestore.getInstance().collection("Player")
    private lateinit var joinedGroup : ArrayList<String>
    private val TAG:String = "GroupLobbyActivity"
    private var userId:String = ""
    private var emailId:String = ""
    private var joinCode:String = ""
    private var userRole:String = ""
    private lateinit var docRef : DocumentReference
    private lateinit var userRef : DocumentReference

    private lateinit var liveData : ConnectionLiveData
    private lateinit var groupLobbyLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_lobby)

        checkNetworkConnection()

        groupLobbyLayout = findViewById(R.id.groupLobbyLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

        userId = intent.getStringExtra("user_id").toString()
        emailId = intent.getStringExtra("email_id").toString()
        joinCode = intent.getStringExtra("join_code").toString()
        docRef = dbGroup.document(joinCode)
        Log.i(TAG, "emailId is $emailId")
        userRef = dbPlayer.document(emailId)
        joinedGroup = ArrayList()

        resetRoles(userRef)

        val joinCodeTv = findViewById<TextView>(R.id.joinCodeActual)
        joinCodeTv.text = joinCode

        val startGameButton = findViewById<Button>(R.id.startGameButton)
        val backToMenuButton = findViewById<Button>(R.id.backToMenuButton)

        startGameButton.setOnClickListener{
            assignRoles()
            subscribeToRealtimeUpdates()
        }

        backToMenuButton.setOnClickListener {
            val intent = Intent(this@GroupLobbyActivity, MainMenuActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("email_id", emailId)
            startActivity(intent)
            this.finish()
        }
    }

    private fun resetRoles(userRef: DocumentReference) {
        val roleData = mutableMapOf<String, Any>()
        roleData["role"] = "none"

        userRef.set(
            roleData,
            SetOptions.merge()
        ).addOnSuccessListener {
            Log.i(TAG, "Roles reset!!")
        }.addOnFailureListener {
            Log.i(TAG, "Roles not reset =(")
        }
    }

    private fun subscribeToRealtimeUpdates() {
        Log.i(TAG, "Subscribing to updates...")
        userRef.addSnapshotListener { query, error ->
            Log.i(TAG, "User Update detected...")
            error?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            query?.let {
                Log.i(TAG, "User Snapshot: ${query.id} => ${query.data}")
                if(query["role"] != "none"){
                    userRole = query["role"].toString()
                    Log.i(TAG, "Starting game as ${userRole}...")
//                    dbPlayer.terminate()
                    sendToAssignedScreen()
                }
            }
        }
    }

    private fun assignRoles() {
        dbGroup.get().addOnSuccessListener { result ->
            for(doc in result){
                Log.i(TAG,"Comparing $joinCode to ${doc.id}...")

                //if matching group is found
                if(doc.id == joinCode){
                    //update Players with the new user
                    Log.i(TAG, "Assigning roles to group members...")
                    Log.d(TAG, "${doc.id} => ${doc.data}")
                    var data:MutableMap<String, Any> = doc.data
                    Log.d(TAG, "${doc.data["Players"]}")
                    val players: MutableList<Any?>? = doc.data["Players"] as MutableList<Any?>?

                    //query for each player in the group and update their roles
                    updatePlayerRoles(players)

//                    data["canProgress"] = true
//                    docRef.set(
//                        data,
//                        SetOptions.merge()
//                    ).addOnSuccessListener {
//                        Log.i(TAG, "Users can now progress to the game.")
//                    }.addOnFailureListener{
//                        Log.w(TAG, "Unable to update canProgress inside of ${doc.id}")
//                    }

                    return@addOnSuccessListener
                }
            }
        }.addOnFailureListener{ exception ->
            Log.w(TAG, "Error getting documents.", exception)
        }
    }

    private fun updatePlayerRoles(players: MutableList<Any?>?){
        var playerCount = 0
        var docRef : DocumentReference
        dbPlayer.get().addOnSuccessListener { result ->
            for (doc in result){
                if (players?.contains(doc.id) == true){
                    docRef = dbPlayer.document(doc.id)
                    playerCount++
                    Log.i(TAG, "Player ${doc.id} found. Updating role...")
                    val playerData:MutableMap<String, Any> = doc.data
                    playerData["role"] = giveRole(playerCount)
                    if(doc.id == emailId){
                        userRole = playerData["role"].toString()
                        Log.i(TAG, "UserRole of $emailId is now $userRole.")
                    }

                    docRef.set(
                        playerData,
                        SetOptions.merge()
                    ).addOnFailureListener{exception ->
                        Log.w(TAG, "Error updating player role.", exception)
                    }
                }
            }
        }
    }

    private fun giveRole(count: Number) : String {
        Log.i(TAG, "Assigning role to user...")
        val role: String = when (count) {
            1 -> {
                "Bookie"
            }
            2 -> {
                "Subject"
            }
            else -> {
                "Bettor"
            }
        }
        return role
    }

    private fun sendToAssignedScreen() {
        when (userRole) {
            "Bookie" -> {
                val intent = Intent(this@GroupLobbyActivity, YouAreBookieActivity::class.java)
                intent.putExtra("user_id", userId)
                intent.putExtra("email_id", emailId)
                intent.putExtra("join_code", joinCode)
                intent.putExtra("user_role", userRole)
                startActivity(intent)
                this.finish()
            }
            "Subject" -> {
                val intent = Intent(this@GroupLobbyActivity, SubjectActivity::class.java)
                intent.putExtra("user_id", userId)
                intent.putExtra("email_id", emailId)
                intent.putExtra("join_code", joinCode)
                intent.putExtra("user_role", userRole)
                startActivity(intent)
                this.finish()
            }
            else -> {
                val intent = Intent(this@GroupLobbyActivity, YouAreBettorActivity::class.java)
                intent.putExtra("user_id", userId)
                intent.putExtra("email_id", emailId)
                intent.putExtra("join_code", joinCode)
                intent.putExtra("user_role", userRole)

                Log.i(TAG, "Sending $emailId...")
                Log.i(TAG, "Sending $joinCode...")

                startActivity(intent)
                this.finish()
            }
        }

        Toast.makeText(
            this@GroupLobbyActivity,
            "You do not have an assigned role",
            Toast.LENGTH_SHORT
        ).show()
    }

//    override fun onViewCreated(savedInstanceState: Bundle?) {
//        viewModel.players.observe(this, Observer {
//                players -> spnPlayers.setAdapter(ArrayAdapter(context!!, R.layout.players_spinner_dropdown), players)
//        })
//    }
private fun checkNetworkConnection(){
    liveData = ConnectionLiveData(application)
    liveData.observe(this) { isConnected ->

        if (isConnected) {
            groupLobbyLayout.visibility = View.VISIBLE
            noWifiLayout.visibility = View.GONE

        } else {
            groupLobbyLayout.visibility = View.GONE
            noWifiLayout.visibility = View.VISIBLE
        }
    }
}
}
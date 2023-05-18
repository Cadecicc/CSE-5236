package com.example.friendlyfire.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.friendlyfire.Model.ConnectionLiveData
import com.example.friendlyfire.R

class SubjectResultActivity : AppCompatActivity() {

    private lateinit var liveData : ConnectionLiveData
    private lateinit var subjectResultsLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_result)

        checkNetworkConnection()

        subjectResultsLayout = findViewById(R.id.subjectResultsLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

        val betDescription = intent.getStringExtra("bet_description").toString()
        Log.i("SubjectResultActivity", "betDescription: $betDescription")

        val mainMenuButton = findViewById<Button>(R.id.backToMenuButton)
        val descriptionView = findViewById<TextView>(R.id.description)
        descriptionView.text = betDescription

        mainMenuButton.setOnClickListener {
            val intent = Intent(this@SubjectResultActivity, MainMenuActivity::class.java)
//            intent.putExtra("user_id", userId)
//            intent.putExtra("email_id", emailId)
            startActivity(intent)
            this.finish()
        }
    }
    private fun checkNetworkConnection(){
        liveData = ConnectionLiveData(application)
        liveData.observe(this) { isConnected ->

            if (isConnected) {
                subjectResultsLayout.visibility = View.VISIBLE
                noWifiLayout.visibility = View.GONE

            } else {
                subjectResultsLayout.visibility = View.GONE
                noWifiLayout.visibility = View.VISIBLE
            }
        }
    }
}
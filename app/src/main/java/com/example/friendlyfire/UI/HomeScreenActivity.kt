package com.example.friendlyfire.UI

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.friendlyfire.Model.ConnectionLiveData
import com.example.friendlyfire.R

class HomeScreenActivity : AppCompatActivity() {
    private lateinit var FFObserver: FFObserver

    private lateinit var liveData : ConnectionLiveData
    private lateinit var homeScreenLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        checkNetworkConnection()

        homeScreenLayout = findViewById(R.id.homeScreenLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

        FFObserver = FFObserver(this, lifecycle)
        lifecycle.addObserver(FFObserver)

        val signInButton = findViewById<Button>(R.id.SignIn)
        signInButton.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        val registerButton = findViewById<Button>(R.id.Register)
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    private fun checkNetworkConnection(){
        liveData = ConnectionLiveData(application)
        liveData.observe(this) { isConnected ->

            if (isConnected) {
                homeScreenLayout.visibility = View.VISIBLE
                noWifiLayout.visibility = View.GONE

            } else {
                homeScreenLayout.visibility = View.GONE
                noWifiLayout.visibility = View.VISIBLE
            }
        }
    }
}